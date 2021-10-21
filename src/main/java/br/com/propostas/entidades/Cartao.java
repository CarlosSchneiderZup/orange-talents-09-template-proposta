package br.com.propostas.entidades;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.repositorios.PropostaRepository;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @OneToOne
    private Proposta proposta;

    @Deprecated
    public Cartao() {

    }

    private Cartao(RespostaCartao resposta, Proposta proposta) {
        this.numeroCartao = resposta.getId();
        this.titular = resposta.getTitular();
        this.dataEmissao = LocalDateTime.parse(resposta.getEmitidoEm());
        this.limite = resposta.getLimite();
        this.vencimento = resposta.getVencimento();
        this.proposta = proposta;
    }

    public static Cartao geraCartao(RespostaCartao resposta, PropostaRepository propostaRepository) {
        Proposta proposta = propostaRepository.findById(Long.valueOf(resposta.getIdProposta())).orElseThrow(() ->new ApiErrorException("Id não encontrada", "id", HttpStatus.NOT_FOUND));
        return new Cartao(resposta, proposta);
    }
}
