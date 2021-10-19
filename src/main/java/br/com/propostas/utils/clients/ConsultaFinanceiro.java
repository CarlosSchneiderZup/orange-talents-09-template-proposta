package br.com.propostas.utils.clients;

import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.SolicitacaoAnalise;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "consultaFinanceira", url = "localhost:9999/api/solicitacao")
public interface ConsultaFinanceiro {

    @PostMapping(consumes = "application/json")
    ResultadoAnalise solicitarConsulta(@RequestBody SolicitacaoAnalise form);
}
