package br.com.propostas.repositorios;

import br.com.propostas.entidades.Bloqueio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueioRepository extends JpaRepository<Bloqueio, Long> {
}
