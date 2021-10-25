package br.com.propostas.controllers;

import br.com.propostas.commons.validators.GaranteDocumentoValidoValidator;
import br.com.propostas.controllers.dtos.NovaUrlDto;
import br.com.propostas.controllers.dtos.PropostaDto;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.controllers.forms.SolicitacaoAnalise;
import br.com.propostas.entidades.Cartao;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.CartaoRepository;
import br.com.propostas.repositorios.PropostaRepository;
import br.com.propostas.utils.clients.ConsultaCartao;
import br.com.propostas.utils.clients.ConsultaFinanceiro;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/propostas")
public class PropostaController {

    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

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
    public ResponseEntity<NovaUrlDto> cadastraProposta(@RequestBody @Valid PropostaForm form, UriComponentsBuilder builder) {

        Proposta proposta = Proposta.montaPropostaValida(form, propostaRepository);
        propostaRepository.save(proposta);
        URI uri = builder.path("/propostas/{id}").buildAndExpand(proposta.getId()).toUri();

        realizarConsultaFinanceira(proposta);

        return ResponseEntity.created(uri).body(new NovaUrlDto(proposta.getId(), "propostas"));
    }

    private void realizarConsultaFinanceira(Proposta proposta) {
        SolicitacaoAnalise solicitacaoAnalise = new SolicitacaoAnalise(proposta.getDocumento(), proposta.getNome(), proposta.getId().toString());
        try {
            ResponseEntity<ResultadoAnalise> resultadoAnalise = consultaFinanceiro.solicitarConsulta(solicitacaoAnalise);
            ResultadoAnalise resultado = resultadoAnalise.getBody();
            proposta.setAvaliacaoFinanceira(resultado.getResultadoSolicitacao());
        } catch (FeignException.UnprocessableEntity e) {
            proposta.setAvaliacaoFinanceira("COM_RESTRICAO");
        } catch ( FeignException e) {
            logger.error("Serviço de consulta de elegibilidade indisponível no momento: " + LocalDateTime.now());
        }
        propostaRepository.save(proposta);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PropostaDto> encontrarPropostaPorId(@PathVariable Long id) {
        Optional<Proposta> propostaInformada = propostaRepository.findById(id);

        if (propostaInformada.isPresent()) {
            Proposta proposta = propostaInformada.get();
            return ResponseEntity.ok(proposta.montaPropostaDto());
        }
        return ResponseEntity.notFound().build();
    }

    @Scheduled(fixedDelayString = "${periodicidade.verificacao-cartao}")
    private void verificaPropostasSemAvaliacao() {
        List<Proposta> propostasSemAvaliacaoFinanceira = propostaRepository.findPropostasSemAvaliacaoFinanceira();

        if (propostasSemAvaliacaoFinanceira.isEmpty()) {
            return;
        }

        for(Proposta proposta: propostasSemAvaliacaoFinanceira) {
            realizarConsultaFinanceira(proposta);
        }
    }

    @Scheduled(fixedDelayString = "${periodicidade.verificacao-cartao}")
    private void associaCartaoAProposta() {
        List<Proposta> propostasSemCartao = propostaRepository.findPropostasElegiveisSemCartao();

        if (propostasSemCartao.isEmpty()) {
            return;
        }

        geraListaDePropostasValidas(propostasSemCartao);

    }

    private void geraListaDePropostasValidas(List<Proposta> propostasSemCartao) {
        List<Proposta> propostasValidas = new ArrayList<>();

        for (Proposta proposta : propostasSemCartao) {
            try {
                ResponseEntity<RespostaCartao> respostaConsulta = consultaCartao.solicitarConsulta(proposta.getId());
                if (respostaConsulta.getStatusCode() == HttpStatus.OK) {
                    RespostaCartao resposta = respostaConsulta.getBody();
                    propostasValidas.add(proposta);
                    Cartao cartao = Cartao.geraCartao(resposta, propostaRepository);
                    cartaoRepository.save(cartao);
                    proposta.setCartao(cartao);
                    logger.info(ofuscaResposta(resposta.getId()) + " para o cartao de proposta " + resposta.getIdProposta());
                }
            } catch (FeignException.FeignClientException e) {
                logger.warn("A solicitação para a proposta de id " + proposta.getId() + " ainda não foi processada");
            } catch (FeignException e) {
                logger.error("Feign está forá do ar em " + LocalDateTime.now());
            }
        }
        propostaRepository.saveAll(propostasValidas);
    }

    private String ofuscaResposta(String id) {
        return id.substring(0, 5) + "*****" + id.substring(id.length() - 2);
    }
}