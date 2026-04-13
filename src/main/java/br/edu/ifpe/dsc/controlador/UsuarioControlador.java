package br.edu.ifpe.dsc.controlador;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import br.edu.ifpe.dsc.model.UsuarioModel;
import br.edu.ifpe.dsc.model.dto.Usuario;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioModel usuarioModel;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST - CADASTRAR
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        try {
            Usuario salvo = usuarioModel.salvarUsuario(usuario);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET - LISTAR
    @GetMapping("/listar")
    public List<Usuario> listar() {
        return usuarioModel.listarUsuarios();
    }

    // GET - BUSCAR POR MATRICULA
    @GetMapping("/buscar/{matricula}")
    public ResponseEntity<Usuario> buscarPorMatricula(@PathVariable String matricula) {
        Optional<Usuario> usuario = usuarioModel.buscarPorMatricula(matricula);
        return usuario.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // PUT - ATUALIZAR
    @PutMapping("/atualizar/{matricula}")
    public ResponseEntity<?> atualizar(@PathVariable String matricula,
                                       @RequestBody Usuario dados) {
        try {
            Usuario atualizado = usuarioModel.atualizarUsuario(matricula, dados);

            if (atualizado == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(atualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/deletar/{matricula}")
    public ResponseEntity<Void> deletar(@PathVariable String matricula) {

        Optional<Usuario> usuario = usuarioModel.buscarPorMatricula(matricula);

        if (usuario.isPresent()) {
            usuarioModel.deletarUsuario(usuario.get());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    //  POST - LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario credenciais) {

        Optional<Usuario> usuarioOpt =
                usuarioModel.buscarPorMatricula(credenciais.getMatricula());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body("Matrícula não encontrada");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(
                credenciais.getSenha(),
                usuario.getSenha())) {

            return ResponseEntity.status(401)
                    .body("Senha incorreta");
        }

        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString(
                        (credenciais.getMatricula() + ":" +
                         credenciais.getSenha()).getBytes()
                );

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("usuario", usuario);
        resposta.put("authHeader", authHeader);

        return ResponseEntity.ok(resposta);
    }
}