package br.edu.ifpe.dsc.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.edu.ifpe.dsc.model.dto.Usuario;
import br.edu.ifpe.dsc.model.repositorios.UsuarioRepositorio;

@Component
public class UsuarioModel {
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public Usuario salvarUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepositorio.findById(id);
    }

    public void deletarUsuario(UUID id) {
        usuarioRepositorio.deleteById(id);
    }

    public Usuario atualizarUsuario(UUID id, Usuario dados) {
        return usuarioRepositorio.findById(id)
            .map(usuario -> {
                usuario.setNome(dados.getNome());
                usuario.setSobrenome(dados.getSobrenome());
                return usuarioRepositorio.save(usuario);
            }).orElse(null);
    }
}
