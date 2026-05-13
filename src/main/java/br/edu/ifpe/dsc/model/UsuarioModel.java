package br.edu.ifpe.dsc.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ifpe.dsc.model.dto.Usuario;
import br.edu.ifpe.dsc.model.repositorios.UsuarioRepositorio;

@Component
public class UsuarioModel {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario salvarUsuario(Usuario usuario) {
        
        // Email gerado automaticamente
        usuario.setEmail(gerarEmail(usuario.getNome(), usuario.getSobrenome(), usuario.getMatricula()));

        // Criptografa senha
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);
        }

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

            // Atualiza o email quando nome e sobrenome forem alterados
            String emailGerado = gerarEmail(usuario.getNome(), usuario.getSobrenome(), usuario.getMatricula());
            usuario.setEmail(emailGerado);

            if (dados.getFuncao() != null) {
                usuario.setFuncao(dados.getFuncao());
            }

            if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
                String senhaCriptografada = passwordEncoder.encode(dados.getSenha());
                usuario.setSenha(senhaCriptografada);
            }

            return usuarioRepositorio.save(usuario);
        }).orElse(null);
}

    private String gerarEmail(String nome, String sobrenome, String matriculaAtual) {
    String base = nome.toLowerCase() + "." + sobrenome.toLowerCase();
    String email = base + "@dsc.com";
    int contador = 1;

    while (usuarioRepositorio.findByEmail(email)
            .filter(usuario -> !usuario.getMatricula().equals(matriculaAtual))
            .isPresent()) {
        email = base + contador + "@dsc.com";
        contador++;
    }

    return email;
}
}