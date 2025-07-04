package br.edu.ifpe.dsc.controlador;

import java.util.List;
import java.util.Optional;

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

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        try {
            Usuario salvo = usuarioModel.salvarUsuario(usuario);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //GET - LSITAR
    @GetMapping("/listar")
    public List<Usuario> listar() {
        return usuarioModel.listarUsuarios();
    }

    //GET - BUSCAR COM MATRICULA
    @GetMapping("/buscar/{matricula}")
    public ResponseEntity<Usuario> buscarPorMatricula(@PathVariable String matricula) {
        Optional<Usuario> usuario = usuarioModel.buscarPorMatricula(matricula);
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //PUT - ATUALIZAR COM MATRICULA
    @PutMapping("/atualizar/{matricula}")
    public ResponseEntity<Usuario> atualizar(@PathVariable String matricula, @RequestBody Usuario dados) {
        try {
            Usuario atualizado = usuarioModel.atualizarUsuario(matricula, dados);
            if (atualizado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //DELETE - DELETAR COM MATRICULA
    @DeleteMapping("/deletar/{matricula}")
    public ResponseEntity<Void> deletar(@PathVariable String matricula) {
    Optional<Usuario> usuario = usuarioModel.buscarPorMatricula(matricula);

    if (usuario.isPresent()) {
        usuarioModel.deletarUsuario(usuario.get());
        return ResponseEntity.noContent().build();
    }

    return ResponseEntity.notFound().build();
}
}