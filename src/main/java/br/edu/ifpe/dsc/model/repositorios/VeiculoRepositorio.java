package br.edu.ifpe.dsc.model.repositorios;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpe.dsc.model.dto.Veiculo;

@Repository
public interface VeiculoRepositorio extends JpaRepository<Veiculo, UUID> {
    Optional<Veiculo> findByNumero(int numero);
}