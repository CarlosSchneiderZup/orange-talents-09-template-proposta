package br.com.propostas.repositorios;

import br.com.propostas.entidades.Biometria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiometriaRepository extends JpaRepository<Biometria, Long> {
}
