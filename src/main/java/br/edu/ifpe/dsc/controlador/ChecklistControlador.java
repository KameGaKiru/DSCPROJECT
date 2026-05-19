package br.edu.ifpe.dsc.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ifpe.dsc.model.ChecklistModel;
import br.edu.ifpe.dsc.model.dto.Checklist;

@RestController
@RequestMapping("/api/checklist")
@CrossOrigin(origins = "*")
public class ChecklistControlador {

    @Autowired
    private ChecklistModel checklistModel;

    // POST - CADASTRAR (motorista)
    // POST /api/checklist/cadastrar/{matricula}/{numeroVeiculo}
    @PostMapping("/cadastrar/{matricula}/{numeroVeiculo}")
    public ResponseEntity<?> cadastrar(
            @PathVariable String matricula,
            @PathVariable int numeroVeiculo,
            @RequestBody Checklist checklist) {

        try {
            Checklist salvo = checklistModel.salvarChecklist(matricula, numeroVeiculo, checklist);
            return ResponseEntity.ok(salvo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro interno ao salvar checklist.");
        }
    }

    // GET - LISTAR TODOS
    // GET /api/checklist/listar
    @GetMapping("/listar")
    public ResponseEntity<List<Checklist>> listar() {
        return ResponseEntity.ok(checklistModel.listarTodos());
    }

    // PUT - REGISTRAR SOLUÇÃO (mecânico)
    // PUT /api/checklist/solucao/{id}/{matriculaMecanico}
    @PutMapping("/solucao/{id}/{matriculaMecanico}")
    public ResponseEntity<?> registrarSolucao(
            @PathVariable Long id,
            @PathVariable String matriculaMecanico,
            @RequestBody Map<String, String> body) {

        try {
            String solucao = body.get("solucao");
            Checklist atualizado = checklistModel.registrarSolucao(id, matriculaMecanico, solucao);
            return ResponseEntity.ok(atualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro interno ao registrar solução.");
        }
    }
}