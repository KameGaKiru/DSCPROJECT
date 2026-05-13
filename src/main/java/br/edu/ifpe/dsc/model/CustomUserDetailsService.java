package br.edu.ifpe.dsc.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import br.edu.ifpe.dsc.model.repositorios.UsuarioRepositorio;
import br.edu.ifpe.dsc.model.dto.Usuario;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String matricula) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(usuario.getMatricula()) // login é a matrícula
                .password(usuario.getSenha())     // senha já criptografada com BCrypt
                .roles(usuario.getFuncao().name()) // converte enum para String ("MOTORISTA", "COORDENADOR", "MECANICO")
                .build();
    }
}
