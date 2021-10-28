package br.com.propostas.entidades;

import br.com.propostas.controllers.forms.AvisoViagemForm;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class AvisoViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String destino;
    @Column(nullable = false)
    private LocalDate dataTermino;
    private LocalDateTime instante = LocalDateTime.now();
    @Column(nullable = false)
    private String userAgent;
    @Column(nullable = false)
    private String ipSolicitante;

    @ManyToOne
    private Cartao cartao;

    public AvisoViagem(Cartao cartao, AvisoViagemForm form, String userAgent, String ipSolicitante) {
        this.destino = form.getDestino();
        this.dataTermino = form.getDataTermino();
        this.userAgent = userAgent;
        this.ipSolicitante = ipSolicitante;
        this.cartao = cartao;
    }

    public static AvisoViagem montaAvisoViagem(Cartao cartao, AvisoViagemForm form, String userAgent, String remoteAddr) {
        return new AvisoViagem(cartao, form, userAgent, remoteAddr);
    }
}
