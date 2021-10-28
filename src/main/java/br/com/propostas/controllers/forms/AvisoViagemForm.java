package br.com.propostas.controllers.forms;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class AvisoViagemForm {

    @NotBlank
    private String destino;
    @Future
    @NotNull
    private LocalDate dataTermino;

    public AvisoViagemForm(String destino, LocalDate dataTermino) {
        this.destino = destino;
        this.dataTermino = dataTermino;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }
}
