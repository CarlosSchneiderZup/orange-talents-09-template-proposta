package br.com.propostas.controllers.forms;

import javax.validation.constraints.NotBlank;

public class SolicitacaoBloqueio {

    @NotBlank
    private String sistemaResponsavel;

    public SolicitacaoBloqueio(String sistemaResponsavel) {
        this.sistemaResponsavel = sistemaResponsavel;
    }

    public String getSistemaResponsavel() {
        return sistemaResponsavel;
    }
}
