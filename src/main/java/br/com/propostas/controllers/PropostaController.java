package br.com.propostas.controllers;

import br.com.propostas.commons.validators.GaranteDocumentoValidoValidator;
import br.com.propostas.controllers.dtos.NovaPropostaDto;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.controllers.forms.SolicitacaoAnalise;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.PropostaRepository;
import br.com.propostas.utils.clients.ConsultaCartao;
import br.com.propostas.utils.clients.ConsultaFinanceiro;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/propostas")
public class PropostaController {

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private ConsultaFinanceiro consultaFinanceiro;

    @Autowired
    private ConsultaCartao consultaCartao;

    @Autowired
    private GaranteDocumentoValidoValidator garanteDocumentoValidoValidator;

    @InitBinder
    public void configuracoesIniciais(WebDataBinder binder) {
        binder.addValidators(garanteDocumentoValidoValidator);
    }

    @PostMapping
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

    @Scheduled(fixedDelayString = "${periodicidade.verificacao-cartao}")
    private void associaCartaoAProposta() {
        List<Proposta> propostasSemCartao = propostaRepository.findPropostasElegiveisSemCartao();
        List<Proposta> propostasComCartao = new ArrayList<>();

        if(propostasSemCartao.isEmpty()) {
            return;
        }

        for(Proposta proposta : propostasSemCartao) {
            RespostaCartao resposta = consultaCartao.solicitarConsulta(proposta.getId());
            if(resposta != null && resposta.getId() != null) {
                proposta.setNroCartao(resposta.getId());
                propostasComCartao.add(proposta);
                System.out.println(resposta.getId() + " para o cartao de proposta " + resposta.getIdProposta());
            }
        }
        propostaRepository.saveAll(propostasComCartao);
    }
}