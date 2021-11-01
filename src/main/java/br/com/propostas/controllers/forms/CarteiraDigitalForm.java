package br.com.propostas.controllers.forms;

import br.com.propostas.entidades.enums.CarteiraDigitalCadastrada;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CarteiraDigitalForm {

    @NotNull
    @JsonProperty
    private CarteiraDigitalCadastrada carteiraDigital;

    @NotBlank
    @Email
    private String email;

    public CarteiraDigitalForm() {
    }

    public CarteiraDigitalCadastrada getCarteiraDigital() {
        return carteiraDigital;
    }

    public String getEmail() {
        return email;
    }
}
