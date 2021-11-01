package br.com.propostas.services;

import br.com.propostas.controllers.PropostaController;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.entidades.Cartao;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.PropostaRepository;
import br.com.propostas.utils.clients.ConsultaCartao;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.propostas.security.Ofuscador.ofuscaCartao;

@Service
public class AssociaCartaoService {

    private final Logger logger = LoggerFactory.getLogger(AssociaCartaoService.class);

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private ConsultaCartao consultaCartao;

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
                    Cartao cartao = Cartao.geraCartao(resposta);
                    proposta.setCartao(cartao);
                    logger.info(ofuscaCartao(resposta.getId()) + " para o cartao de proposta " + resposta.getIdProposta());
                }
            } catch (FeignException.FeignClientException e) {
                logger.warn("A solicitação para a proposta de id " + proposta.getId() + " ainda não foi processada");
            } catch (FeignException e) {
                logger.error("Feign está forá do ar em " + LocalDateTime.now());
            }
        }
        propostaRepository.saveAll(propostasValidas);
    }
}
