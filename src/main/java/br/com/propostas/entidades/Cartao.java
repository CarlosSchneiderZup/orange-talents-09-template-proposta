package br.com.propostas.entidades;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.entidades.acoplamentos.Bloqueio;
import br.com.propostas.entidades.acoplamentos.Vencimento;
import br.com.propostas.repositorios.PropostaRepository;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String numeroCartao;
    @Column(nullable = false)
    private String titular;
    private LocalDateTime dataEmissao;
    @Column(nullable = false)
    private Integer limite;

    @Embedded
    private Vencimento vencimento;
    @ElementCollection
    private List<Bloqueio> bloqueios = new ArrayList<>();

    @Deprecated
    public Cartao() {

    }

    private Cartao(RespostaCartao resposta) {
        this.numeroCartao = resposta.getId();
        this.titular = resposta.getTitular();
        this.dataEmissao = LocalDateTime.parse(resposta.getEmitidoEm());
        this.limite = resposta.getLimite();
        this.vencimento = resposta.getVencimento();
    }

    public static Cartao geraCartao(RespostaCartao resposta) {
        return new Cartao(resposta);
    }

    public Long getId() {
        return id;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public boolean verificaBloqueioAtivo() {
        if(bloqueios.isEmpty()) {
            return false;
        }
        for(Bloqueio bloqueio : bloqueios) {
            if(bloqueio.getAtivo()) {
                return true;
            }
        }
        return false;
    }

    public void adicionaBloqueio(Bloqueio bloqueio) {
        bloqueios.add(bloqueio);
    }


}
