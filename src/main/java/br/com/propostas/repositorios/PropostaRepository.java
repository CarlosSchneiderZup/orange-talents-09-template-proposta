package br.com.propostas.repositorios;

import br.com.propostas.entidades.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {
}
