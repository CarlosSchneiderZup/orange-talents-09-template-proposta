package br.com.propostas.controllers.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class BiometriaForm {

    @NotBlank
    @JsonProperty
    private String biometria;

    public BiometriaForm() {}

    public BiometriaForm(String biometria) {
        this.biometria = biometria;
    }

    public String getBiometria() {
        return biometria;
    }
}
