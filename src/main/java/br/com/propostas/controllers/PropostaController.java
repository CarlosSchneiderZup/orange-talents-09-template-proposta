package br.com.propostas.controllers;

import br.com.propostas.commons.validators.GaranteDocumentoValidoValidator;
import br.com.propostas.controllers.dtos.NovaPropostaDto;
import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.controllers.forms.SolicitacaoAnalise;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.PropostaRepository;
import br.com.propostas.utils.clients.ConsultaFinanceiro;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/propostas")
public class PropostaController {

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private ConsultaFinanceiro consultaFinanceiro;

    @Autowired
    private GaranteDocumentoValidoValidator garanteDocumentoValidoValidator;

    @InitBinder
    public void configuracoesIniciais(WebDataBinder binder) {
        binder.addValidators(garanteDocumentoValidoValidator);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NovaPropostaDto> cadastraProposta(@RequestBody @Valid PropostaForm form, UriComponentsBuilder builder) throws JsonProcessingException {

        Proposta proposta = Proposta.montaPropostaValida(form, propostaRepository);
        propostaRepository.save(proposta);
        URI uri = builder.path("/propostas/{id}").buildAndExpand(proposta.getId()).toUri();

        SolicitacaoAnalise solicitacaoAnalise = new SolicitacaoAnalise(form.getDocumento(), form.getNome(), proposta.getId().toString());
        ResultadoAnalise resultado = consultaFinanceiro.solicitarConsulta(solicitacaoAnalise);

        proposta.setAvaliacaoFinanceira(resultado.getResultadoSolicitacao());
        propostaRepository.save(proposta);

        return ResponseEntity.created(uri).body(new NovaPropostaDto(proposta.getId()));
    }
}