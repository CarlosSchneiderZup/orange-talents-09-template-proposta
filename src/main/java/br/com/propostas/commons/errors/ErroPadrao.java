package br.com.propostas.commons.errors;

import java.time.LocalDateTime;

public class ErroPadrao {

    String campo;
    String mensagem;
    LocalDateTime instante;

    public ErroPadrao(String campo, String erro, LocalDateTime instante) {
        this.campo = campo;
        this.mensagem = erro;
        this.instante = instante;
    }

    public String getCampo() {
        return campo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getInstante() {
        return instante;
    }
}
