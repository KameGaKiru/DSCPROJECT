package br.edu.ifpe.dsc.model;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        String role = "ROLE_" + usuario.getFuncao().name();

        return new User(
                usuario.getMatricula(),
                usuario.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}