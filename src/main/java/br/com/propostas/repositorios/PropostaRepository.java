package br.com.propostas.repositorios;

import br.com.propostas.entidades.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {
	Optional<Proposta> findByDocumento(String documento);

	@Query(value = "select * from proposta where avaliacao_financeira = 1 and nro_cartao is null", nativeQuery = true)
	List<Proposta> findPropostasElegiveisSemCartao();
}
