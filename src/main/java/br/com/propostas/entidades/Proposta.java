package br.com.propostas.entidades;

import java.math.BigDecimal;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.com.propostas.entidades.enums.AvaliacaoFinanceira;
import org.springframework.http.HttpStatus;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.repositorios.PropostaRepository;

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
    private AvaliacaoFinanceira avaliacaoFinanceira;

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
    	if(proposta.isPresent()) {
    		throw new ApiErrorException("Já existe uma proposta para este documento", "documento", HttpStatus.UNPROCESSABLE_ENTITY);
    	}
    	
    	return new Proposta(form);
    }

    public Long getId() {
        return id;
    }

    public void setAvaliacaoFinanceira(String avaliacao) {
        if (avaliacao.equals("COM_RESTRICAO")) {
            this.avaliacaoFinanceira = AvaliacaoFinanceira.NAO_ELEGIVEL;
        } else {
            this.avaliacaoFinanceira = AvaliacaoFinanceira.ELEGIVEL;
        }
    }
}
