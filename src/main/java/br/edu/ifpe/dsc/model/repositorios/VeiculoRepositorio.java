package br.edu.ifpe.dsc.model.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpe.dsc.model.dto.Veiculo;

@Repository
public interface VeiculoRepositorio extends JpaRepository<Veiculo, Long> {

    Optional<Veiculo> findByNumero(int numero);

    boolean existsByNumero(int numero);

    boolean existsByPlaca(String placa);
}