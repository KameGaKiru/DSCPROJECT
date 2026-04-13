package br.edu.ifpe.dsc.controlador;

import java.util.List;

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

    // POST - CADASTRAR
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
    @GetMapping("/listar")
    public ResponseEntity<List<Checklist>> listar() {
        return ResponseEntity.ok(checklistModel.listarTodos());
    }

    // GET - LISTAR POR MOTORISTA
    @GetMapping("/listar/motorista/{matricula}")
    public ResponseEntity<?> listarPorMotorista(@PathVariable String matricula) {
        try {
            return ResponseEntity.ok(checklistModel.listarPorMotorista(matricula));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET - LISTAR POR VEÍCULO

    @GetMapping("/listar/veiculo/{numeroVeiculo}")
    public ResponseEntity<?> listarPorVeiculo(@PathVariable int numeroVeiculo) {
        try {
            return ResponseEntity.ok(checklistModel.listarPorVeiculo(numeroVeiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}