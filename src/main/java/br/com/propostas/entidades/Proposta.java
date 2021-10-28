package br.com.propostas.entidades;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.dtos.PropostaDto;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.entidades.enums.AvaliacaoFinanceira;
import br.com.propostas.repositorios.PropostaRepository;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Optional;

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
        this.documento = form.getDocumento();
        this.endereco = form.getEndereco();
        this.salario = BigDecimal.valueOf(form.getSalario());
    }

    public static Proposta montaPropostaValida(PropostaForm form, PropostaRepository propostaRepository) {
        Optional<Proposta> proposta = propostaRepository.findByDocumento(form.getDocumento());
        if (proposta.isPresent()) {
            throw new ApiErrorException("Já existe uma proposta para este documento", "documento", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new Proposta(form);
    }

    public PropostaDto montaPropostaDto() {
        return new PropostaDto(id, ofuscaEmail(), ofuscaNome(), ofuscaDocumento(), avaliacaoFinanceira, ofuscaCartao());
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

    private String ofuscaEmail() {
        String[] emailDividido = email.split("@");
        return emailDividido[0].substring(0, 3) + "***@" + emailDividido[1];
    }

    private String ofuscaNome() {
        String[] nomeDividido = nome.split(" ");
        StringBuilder nomeFinal = new StringBuilder();
        for (int i = 0; i < nomeDividido.length; i++) {
            nomeFinal.append(nomeDividido[i].substring(0, 1) + ". ");
        }
        return nomeFinal.toString();
    }

    private String ofuscaDocumento() {
        return documento.substring(0, 3) + "***" + documento.substring(documento.length() - 2);
    }

    private String ofuscaCartao() {
        if (cartao == null) {
            return null;
        }
        String nroCartao = cartao.getNumeroCartao();
        return nroCartao.substring(0, 4) + "***" + nroCartao.substring(nroCartao.length() - 2);
    }
}
