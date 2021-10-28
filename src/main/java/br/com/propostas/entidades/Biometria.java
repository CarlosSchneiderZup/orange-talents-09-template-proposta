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
    @Column(nullable = false, columnDefinition = "TEXT")
    private String biometria;

    @ManyToOne
    @JoinColumn(name = "cartao_id", nullable = false)
    private Cartao cartao;

    @Deprecated
    public Biometria() {

    }

    private Biometria(BiometriaForm form, Cartao cartao) {
        this.biometria = form.getBiometria();
        this.cartao = cartao;

    }

    public static Biometria montaNovaBiometria(BiometriaForm form, Cartao cartao) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            decoder.decode(form.getBiometria());
            return new Biometria(form, cartao);
        } catch(IllegalArgumentException e) {
            throw new ApiErrorException("A String informada não é um Base64", "biometria", HttpStatus.BAD_REQUEST);
        }

    }

    public Long getId() {
        return id;
    }
}
