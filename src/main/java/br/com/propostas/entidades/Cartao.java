package br.com.propostas.entidades;

import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.entidades.acoplamentos.Vencimento;
import br.com.propostas.entidades.enums.CarteiraDigitalCadastrada;
import br.com.propostas.entidades.enums.StatusBloqueio;

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
    @Enumerated(EnumType.STRING)
    private StatusBloqueio statusBloqueio = StatusBloqueio.LIBERADO;

    @OneToMany(mappedBy = "cartao")
    private List<CarteiraDigital> carteiras = new ArrayList<>();

    @Embedded
    private Vencimento vencimento;

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
        return statusBloqueio.equals(StatusBloqueio.BLOQUEADO);
    }

    public void bloquear() {
        statusBloqueio = StatusBloqueio.BLOQUEADO;
    }

    public boolean verificaDuplicidadeDeCarteira(CarteiraDigitalCadastrada possivelCarteira) {
        if(carteiras.isEmpty()) {
            return false;
        }
        for(CarteiraDigital carteira : carteiras) {
            if(carteira.getCarteiraDigitalCadastrada().equals(possivelCarteira)) {
                return true;
            }
        }
        return false;
    }

    public void adicionaNovaCarteira(CarteiraDigital carteiraDigital) {
        carteiras.add(carteiraDigital);
    }
}
