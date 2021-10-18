package br.com.propostas.entidades;

import br.com.propostas.controllers.forms.PropostaForm;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private String documento;
    @Column(nullable = false)
    private String endereco;
    @Column(nullable = false)
    private BigDecimal salario;

    @Deprecated
    public Proposta() {

    }

    public Proposta(PropostaForm form) {
        this.email = form.getEmail();
        this.nome = form.getNome();
        this.documento = form.getDocumento();
        this.endereco = form.getEndereco();
        this.salario = BigDecimal.valueOf(form.getSalario());
    }

    public Long getId() {
        return id;
    }
}
