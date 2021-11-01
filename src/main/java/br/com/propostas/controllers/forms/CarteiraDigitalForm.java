package br.com.propostas.controllers.forms;

import br.com.propostas.entidades.enums.CarteiraDigitalCadastrada;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class CarteiraDigitalForm {

    @NotBlank
    @JsonProperty
    private CarteiraDigitalCadastrada carteiraDigital;

    public CarteiraDigitalForm() {
    }

    public CarteiraDigitalCadastrada getCarteiraDigital() {
        return carteiraDigital;
    }
}
