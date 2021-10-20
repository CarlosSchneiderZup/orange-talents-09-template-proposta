package br.com.propostas.utils.clients;

import br.com.propostas.controllers.dtos.RespostaCartao;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "consultaCartao", url = "localhost:8888/api/cartoes")
public interface ConsultaCartao {

    @GetMapping(value = "?idProposta={id}")
    ResponseEntity<RespostaCartao> solicitarConsulta(@PathVariable long id);
}
