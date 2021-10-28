package br.com.propostas.entidades;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Bloqueio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime instante;
    @Column(nullable = false)
    private String ipSolicitante;
    @Column(nullable = false)
    private String userAgent;
    private Boolean ativo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Cartao cartao;

    @Deprecated
    public Bloqueio() {}

    public Bloqueio(String ipSolicitante, String userAgent, Boolean ativo, Cartao cartao) {
        this.instante = LocalDateTime.now();
        this.ipSolicitante = ipSolicitante;
        this.userAgent = userAgent;
        this.ativo = ativo;
        this.cartao = cartao;
    }

    public LocalDateTime getInstante() {
        return instante;
    }

    public String getIpSolicitante() {
        return ipSolicitante;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}
