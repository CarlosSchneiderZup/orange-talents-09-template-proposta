package br.com.propostas.entidades.acoplamentos;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class Bloqueio {

    private LocalDateTime instante;
    private String ipSolicitante;
    private String userAgent;
    private Boolean ativo;

    @Deprecated
    public Bloqueio() {}

    public Bloqueio(String ipSolicitante, String userAgent, Boolean ativo) {
        this.instante = LocalDateTime.now();
        this.ipSolicitante = ipSolicitante;
        this.userAgent = userAgent;
        this.ativo = ativo;
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
