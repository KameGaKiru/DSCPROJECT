package br.edu.ifpe.dsc.model.repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.dsc.model.dto.Veiculo;

public interface VeiculoRepositorio extends JpaRepository<Veiculo, UUID>{

    
}