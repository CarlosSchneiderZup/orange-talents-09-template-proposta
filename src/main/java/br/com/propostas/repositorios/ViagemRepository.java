package br.com.propostas.repositorios;

import br.com.propostas.entidades.AvisoViagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViagemRepository extends JpaRepository<AvisoViagem, Long> {
}
