package br.com.propostas.entidades;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.dtos.PropostaDto;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.entidades.enums.AvaliacaoFinanceira;
import br.com.propostas.repositorios.PropostaRepository;
import br.com.propostas.services.Encriptador;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Optional;

import static br.com.propostas.security.Ofuscador.*;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private String documento;
    @Column(nullable = false)
    private String endereco;
    @Column(nullable = false)
    private BigDecimal salario;
    private AvaliacaoFinanceira avaliacaoFinanceira = AvaliacaoFinanceira.EM_ANALISE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nro_cartao")
    private Cartao cartao;

    @Deprecated
    public Proposta() {

    }

    private Proposta(PropostaForm form) {
        this.email = form.getEmail();
        this.nome = form.getNome();
        this.documento = Encriptador.encriptar(form.getDocumento());
        this.endereco = form.getEndereco();
        this.salario = BigDecimal.valueOf(form.getSalario());
    }

    public static Proposta montaPropostaValida(PropostaForm form, PropostaRepository propostaRepository) {
        Optional<Proposta> proposta = propostaRepository.findByDocumento(Encriptador.encriptar(form.getDocumento()));
        if (proposta.isPresent()) {
            throw new ApiErrorException("Já existe uma proposta para este documento", "documento", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new Proposta(form);
    }

    public PropostaDto montaPropostaDto() {
        return new PropostaDto(id, ofuscaEmail(email), ofuscaNome(nome), ofuscaDocumento(Encriptador.decriptar(documento)), avaliacaoFinanceira, ofuscaCartao(cartao));
    }

    public Long getId() {
        return id;
    }

    public void setAvaliacaoFinanceira(String avaliacao) {
        if (avaliacao.equals("COM_RESTRICAO")) {
            this.avaliacaoFinanceira = AvaliacaoFinanceira.NAO_ELEGIVEL;
        } else if (avaliacao.equals("SEM_RESTRICAO")) {
            this.avaliacaoFinanceira = AvaliacaoFinanceira.ELEGIVEL;
        } else {
            throw new ApiErrorException("Formato de avaliação financeira invalida", "avaliacaoFinanceira", HttpStatus.BAD_REQUEST);
        }
    }

    public String getDocumento() {
        return documento;
    }

    public String getNome() {
        return nome;
    }

    public AvaliacaoFinanceira getAvaliacaoFinanceira() {
        return avaliacaoFinanceira;
    }

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }
}
