package br.com.propostas.security;

import br.com.propostas.entidades.Cartao;

public class Ofuscador {

    public static String ofuscaCartao(String id) {
        return id.substring(0, 5) + "*****" + id.substring(id.length() - 2);
    }

    public static String ofuscaNome(String nome) {
        String[] nomeDividido = nome.split(" ");
        StringBuilder nomeFinal = new StringBuilder();
        for (int i = 0; i < nomeDividido.length; i++) {
            nomeFinal.append(nomeDividido[i].substring(0, 1) + ". ");
        }
        return nomeFinal.toString();
    }

    public static String ofuscaEmail(String email) {
        String[] emailDividido = email.split("@");
        return emailDividido[0].substring(0, 3) + "***@" + emailDividido[1];
    }

    public static String ofuscaDocumento(String documento) {
        return documento.substring(0, 3) + "***" + documento.substring(documento.length() - 2);
    }

    public static String ofuscaCartao(Cartao cartao) {
        if (cartao == null) {
            return null;
        }
        String nroCartao = cartao.getNumeroCartao();
        return nroCartao.substring(0, 4) + "***" + nroCartao.substring(nroCartao.length() - 2);
    }
}
