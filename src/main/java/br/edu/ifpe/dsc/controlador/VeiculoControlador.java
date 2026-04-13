package br.edu.ifpe.dsc.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ifpe.dsc.model.VeiculoModel;
import br.edu.ifpe.dsc.model.dto.Veiculo;

@RestController
@RequestMapping("/api/veiculo")
public class VeiculoControlador {

    @Autowired
    private VeiculoModel veiculoModel;

    // POST - CADASTRAR
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Veiculo veiculo) {
        try {
            Veiculo salvo = veiculoModel.salvar(veiculo);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET - LISTAR TODOS
    @GetMapping("/listar")
    public List<Veiculo> listar() {
        return veiculoModel.listarTodos();
    }

    // GET - BUSCAR POR NUMERO
    @GetMapping("/buscar/{numero}")
    public ResponseEntity<Veiculo> buscarPorNumero(@PathVariable int numero) {
        Optional<Veiculo> veiculo = veiculoModel.buscarPorNumero(numero);
        return veiculo.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // PUT - ATUALIZAR POR NUMERO
    @PutMapping("/atualizar/{numero}")
    public ResponseEntity<?> atualizar(@PathVariable int numero,
                                       @RequestBody Veiculo dados) {
        try {
            Veiculo atualizado = veiculoModel.atualizar(numero, dados);
            if (atualizado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE - POR NUMERO
    @DeleteMapping("/deletar/{numero}")
    public ResponseEntity<Void> deletar(@PathVariable int numero) {
        Optional<Veiculo> veiculo = veiculoModel.buscarPorNumero(numero);

        if (veiculo.isPresent()) {
            veiculoModel.deletarVeiculo(veiculo.get());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}