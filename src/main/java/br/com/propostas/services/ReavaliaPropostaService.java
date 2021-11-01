package br.com.propostas.services;

import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.SolicitacaoAnalise;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.PropostaRepository;
import br.com.propostas.utils.clients.ConsultaFinanceiro;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReavaliaPropostaService {

    private final Logger logger = LoggerFactory.getLogger(ReavaliaPropostaService.class);

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private ConsultaFinanceiro consultaFinanceiro;

    @Scheduled(fixedDelayString = "${periodicidade.verificacao-cartao}")
    private void verificaPropostasSemAvaliacao() {
        List<Proposta> propostasSemAvaliacaoFinanceira = propostaRepository.findPropostasSemAvaliacaoFinanceira();

        if (propostasSemAvaliacaoFinanceira.isEmpty()) {
            return;
        }

        realizarConsultaFinanceira(propostasSemAvaliacaoFinanceira);
    }

    private void realizarConsultaFinanceira(List<Proposta> propostas) {
        List<Proposta> propostasAvaliadas = new ArrayList<>();

        for(Proposta proposta : propostas) {
            SolicitacaoAnalise solicitacaoAnalise = new SolicitacaoAnalise(proposta.getDocumento(), proposta.getNome(), proposta.getId().toString());
            try {
                ResponseEntity<ResultadoAnalise> resultadoAnalise = consultaFinanceiro.solicitarConsulta(solicitacaoAnalise);
                ResultadoAnalise resultado = resultadoAnalise.getBody();
                proposta.setAvaliacaoFinanceira(resultado.getResultadoSolicitacao());
                propostasAvaliadas.add(proposta);
            } catch (FeignException.UnprocessableEntity e) {
                proposta.setAvaliacaoFinanceira("COM_RESTRICAO");
                propostasAvaliadas.add(proposta);
            } catch (FeignException e) {
                logger.error("Serviço de consulta de elegibilidade indisponível no momento: " + LocalDateTime.now());
            }
        }
        propostaRepository.saveAll(propostasAvaliadas);
    }
}
