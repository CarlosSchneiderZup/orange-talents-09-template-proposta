package br.com.propostas.entidades;

import br.com.propostas.controllers.dtos.ResultadoCarteira;
import br.com.propostas.controllers.forms.CarteiraDigitalForm;
import br.com.propostas.entidades.enums.CarteiraDigitalCadastrada;

import javax.persistence.*;

@Entity
public class CarteiraDigital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String chaveCarteira;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CarteiraDigitalCadastrada carteiraDigitalCadastrada;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cartao_id", nullable = false)
    private Cartao cartao;

    @Deprecated
    public CarteiraDigital() {}

    private CarteiraDigital(String chaveCarteira, CarteiraDigitalCadastrada carteiraDigitalCadastrada, Cartao cartao) {
        this.chaveCarteira = chaveCarteira;
        this.carteiraDigitalCadastrada = carteiraDigitalCadastrada;
        this.cartao = cartao;
    }

    public static CarteiraDigital montaCarteiraDigital(Cartao cartao, CarteiraDigitalCadastrada carteira, ResultadoCarteira resultado) {
        return new CarteiraDigital(resultado.getId(), carteira, cartao);
    }

    public Long getId() {
        return id;
    }

    public CarteiraDigitalCadastrada getCarteiraDigitalCadastrada() {
        return carteiraDigitalCadastrada;
    }
}
