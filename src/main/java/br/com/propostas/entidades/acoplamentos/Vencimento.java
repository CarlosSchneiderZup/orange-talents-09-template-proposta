package br.com.propostas.entidades.acoplamentos;

import javax.persistence.Embeddable;

@Embeddable
public class Vencimento {

    private String idVencimento;
    private Integer dia;
    private String dataCriacao;

    public String getIdVencimento() {
        return idVencimento;
    }

    public Integer getDia() {
        return dia;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }
}
