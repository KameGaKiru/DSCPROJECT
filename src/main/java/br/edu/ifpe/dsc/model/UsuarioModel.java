package br.edu.ifpe.dsc.model;

import java.util.List;
import java.util.Optional;

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

    public Optional<Usuario> buscarPorMatricula(String matricula) {
        return usuarioRepositorio.findByMatricula(matricula);
    }

    public void deletarUsuario(Usuario usuario) {
    usuarioRepositorio.delete(usuario);
}  

    public Usuario atualizarUsuario(String matricula, Usuario dados) {
    return usuarioRepositorio.findByMatricula(matricula)
        .map(usuario -> {
            if (dados.getNome() != null) {
                usuario.setNome(dados.getNome());
            }
            if (dados.getSobrenome() != null) {
                usuario.setSobrenome(dados.getSobrenome());
            }
            if (dados.getEmail() != null) {
                usuario.setEmail(dados.getEmail());
            }
            if (dados.getFuncao() != null) {
                usuario.setFuncao(dados.getFuncao());
            }
            return usuarioRepositorio.save(usuario);
        }).orElse(null);
}
}
