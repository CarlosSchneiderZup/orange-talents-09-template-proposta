package br.com.propostas.controllers.dtos;

import br.com.propostas.entidades.enums.AvaliacaoFinanceira;

public class PropostaDto {

    private Long id;
    private String email;
    private String nome;
    private String documento;
    private AvaliacaoFinanceira avaliacaoFinanceira;
    private String nroCartao;

    public PropostaDto(Long id, String email, String nome, String documento, AvaliacaoFinanceira avaliacaoFinanceira, String nroCartao) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.documento = documento;
        this.avaliacaoFinanceira = avaliacaoFinanceira;
        this.nroCartao = nroCartao;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getDocumento() {
        return documento;
    }

    public AvaliacaoFinanceira getAvaliacaoFinanceira() {
        return avaliacaoFinanceira;
    }

    public String getNroCartao() {
        return nroCartao;
    }
}
