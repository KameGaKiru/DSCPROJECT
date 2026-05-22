package br.edu.ifpe.dsc.model.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpe.dsc.model.dto.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByMatricula(String matricula);
    
    Optional<Usuario> findByEmail(String email);

    boolean existsByMatricula(String matricula);
}