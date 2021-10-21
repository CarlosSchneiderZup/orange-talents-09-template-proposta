package br.com.propostas.repositorios;

import br.com.propostas.entidades.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {
}
