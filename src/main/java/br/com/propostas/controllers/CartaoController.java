package br.com.propostas.controllers;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.dtos.RespostaBloqueioCartao;
import br.com.propostas.controllers.forms.SolicitacaoBloqueio;
import br.com.propostas.entidades.Cartao;
import br.com.propostas.entidades.acoplamentos.Bloqueio;
import br.com.propostas.repositorios.CartaoRepository;
import br.com.propostas.utils.clients.ConsultaCartao;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final Logger logger = LoggerFactory.getLogger(CartaoController.class);

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private ConsultaCartao consultaCartao;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/bloqueios/{id}")
    public void solicitaBloqueio(@PathVariable Long id, @RequestHeader("User-Agent") String userAgent, HttpServletRequest request) {
        Cartao cartao = cartaoRepository.findById(id).orElseThrow(() -> new ApiErrorException("Cartão não encontrado", "id", HttpStatus.NOT_FOUND));

        if(cartao.verificaBloqueioAtivo()) {
            logger.error("Tentativa de bloqueio para o cartão de id " + id + ", que já possui um bloqueio ativo" );
            throw new ApiErrorException("Este cartão ja possui um bloqueio ativo", "bloqueios", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Bloqueio bloqueio = montaBloqueio(cartao, request.getRemoteAddr(), userAgent);
        cartao.adicionaBloqueio(bloqueio);
        cartaoRepository.save(cartao);

    }

    private Bloqueio montaBloqueio(Cartao cartao, String ip, String userAgent) {
        try {
            ResponseEntity<RespostaBloqueioCartao> resposta = consultaCartao.solicitarBloqueio(cartao.getNumeroCartao(), new SolicitacaoBloqueio("Proposta"));

                RespostaBloqueioCartao respostaBloqueio = resposta.getBody();

                if(respostaBloqueio.getResultado().equals("FALHA")) {
                    logger.error("Falha ao buscar o resultado da Api para o cartao " + ofuscaResposta(cartao.getNumeroCartao()));
                    throw new ApiErrorException("Falha ao realizar o bloqueio", "bloqueio", HttpStatus.BAD_REQUEST);
                }

        } catch(FeignException e) {
            logger.error("O serviço de cartões está indisponível em " + LocalDateTime.now());
            throw new ApiErrorException("O serviço de bloqueio está temporariamente indisponível, tente novamente mais tarde", "cartao", HttpStatus.BAD_REQUEST);
        }
        return new Bloqueio(ip, userAgent, true);
    }

    private String ofuscaResposta(String id) {
        return id.substring(0, 5) + "*****" + id.substring(id.length() - 2);
    }
}
