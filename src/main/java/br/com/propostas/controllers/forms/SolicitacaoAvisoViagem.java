package br.com.propostas.controllers.forms;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class SolicitacaoAvisoViagem {

    private String destino;
    private String dataRetorno;

    public SolicitacaoAvisoViagem(String destino, String dataRetorno) {
        this.destino = destino;
        this.dataRetorno = dataRetorno;
    }

    public String getDestino() {
        return destino;
    }

    public String getDataRetorno() {
        return dataRetorno;
    }
}
