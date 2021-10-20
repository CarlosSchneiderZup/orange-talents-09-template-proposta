package br.com.propostas.controllers.dtos;

public class RespostaCartao {
    private String id;
    private String emitidoEm;
    private String titular;
    private String idProposta;
    private Integer limite;

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
}
