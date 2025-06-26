package br.edu.ifpe.dsc.model.repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.dsc.model.dto.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, UUID> {

    
}