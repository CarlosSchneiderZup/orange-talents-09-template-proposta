package br.com.propostas.utils.clients;

import br.com.propostas.controllers.dtos.RespostaAvisoViagem;
import br.com.propostas.controllers.dtos.RespostaBloqueioCartao;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.controllers.dtos.ResultadoCarteira;
import br.com.propostas.controllers.forms.SolicitacaoAvisoViagem;
import br.com.propostas.controllers.forms.SolicitacaoBloqueio;
import br.com.propostas.controllers.forms.SolicitacaoInclusaoCarteira;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "consultaCartao", url = "localhost:8888/api/cartoes")
public interface ConsultaCartao {

    @GetMapping(value = "?idProposta={id}")
    ResponseEntity<RespostaCartao> solicitarConsulta(@PathVariable long id);

    @PostMapping(value = "/{id}/bloqueios", consumes = "application/json")
    ResponseEntity<RespostaBloqueioCartao> solicitarBloqueio(@PathVariable String id, @RequestBody SolicitacaoBloqueio solicitacao);

    @PostMapping(value = "/{id}/avisos", consumes = "application/json")
    ResponseEntity<RespostaAvisoViagem> solicitarViagem(@PathVariable String id, @RequestBody SolicitacaoAvisoViagem solicitacao);

    @PostMapping(value = "/{id}/carteiras", consumes = "application/json")
    ResultadoCarteira solicitarNovaCarteira(@PathVariable String id, @RequestBody SolicitacaoInclusaoCarteira solicitacao);

}
