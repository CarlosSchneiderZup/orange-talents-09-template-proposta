package br.com.propostas.controllers.dtos;

import br.com.propostas.entidades.Vencimento;

public class RespostaCartao {
    private String id;
    private String emitidoEm;
    private String titular;
    private String idProposta;
    private Integer limite;
    private Vencimento vencimento;

    public String getId() {
        return id;
    }

    public String getEmitidoEm() {
        return emitidoEm;
    }

    public String getTitular() {
        return titular;
    }

    public String getIdProposta() {
        return idProposta;
    }

    public Integer getLimite() {
        return limite;
    }

    public Vencimento getVencimento() {
        return vencimento;
    }
}
