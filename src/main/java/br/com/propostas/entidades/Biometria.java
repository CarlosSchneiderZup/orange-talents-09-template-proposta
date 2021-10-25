package br.com.propostas.entidades;

import br.com.propostas.controllers.forms.BiometriaForm;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
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
        byte[] byteBiometria = form.getBiometria().getBytes();

        this.biometria = Base64.getEncoder().encodeToString(byteBiometria);
        this.proposta = proposta;
    }

    public static Biometria montaNovaBiometria(BiometriaForm form, Proposta proposta) {
        return new Biometria(form, proposta);
    }

    public Long getId() {
        return id;
    }
}
