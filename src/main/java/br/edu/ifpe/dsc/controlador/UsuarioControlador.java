package br.edu.ifpe.dsc.controlador;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.dsc.model.UsuarioModel;
import br.edu.ifpe.dsc.model.dto.Usuario;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioModel usuarioModel;

   // POST - Cadastrar usuário
    @PostMapping("/cadastrar")
    public Usuario saveUsuario(@RequestBody Usuario usuario) {
        return usuarioModel.salvarUsuario(usuario);
    }

    // GET - Listar todos
    @GetMapping("/listar")
    public List<Usuario> listar() {
        return usuarioModel.listarUsuarios();
    }

    // GET - Buscar por ID
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable UUID id) {
        Optional<Usuario> usuario = usuarioModel.buscarPorId(id);
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // PUT - Atualizar
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable UUID id, @RequestBody Usuario dados) {
        Usuario atualizado = usuarioModel.atualizarUsuario(id, dados);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(atualizado);
    }

    // DELETE - Remover
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Optional<Usuario> usuario = usuarioModel.buscarPorId(id);
        if (usuario.isPresent()) {
            usuarioModel.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}