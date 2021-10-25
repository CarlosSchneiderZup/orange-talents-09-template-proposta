package br.com.propostas.entidades;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.forms.BiometriaForm;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.util.Base64;

@Entity
public class Biometria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String biometria;

    @ManyToOne
    @JoinColumn(name = "proposta_id", nullable = false)
    private Proposta proposta;

    @Deprecated
    public Biometria() {

    }

    private Biometria(BiometriaForm form, Proposta proposta) {
        this.biometria = form.getBiometria();
        this.proposta = proposta;

    }

    public static Biometria montaNovaBiometria(BiometriaForm form, Proposta proposta) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            decoder.decode(form.getBiometria());
            return new Biometria(form, proposta);
        } catch(IllegalArgumentException e) {
            throw new ApiErrorException("A String informada não é um Base64", "biometria", HttpStatus.BAD_REQUEST);
        }

    }

    public Long getId() {
        return id;
    }
}
