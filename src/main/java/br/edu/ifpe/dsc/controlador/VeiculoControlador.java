package br.edu.ifpe.dsc.controlador;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    // POST - Cadastrar veículo
    @PostMapping("/cadastrar")
    public Veiculo cadastrarVeiculo(@RequestBody Veiculo veiculo) {
        return veiculoModel.salvarVeiculo(veiculo);
    }

    // GET - Listar todos os veículos
    @GetMapping("/listar")
    public List<Veiculo> listar() {
        return veiculoModel.listarVeiculos();
    }

    // GET - Buscar veículo por ID
    @GetMapping("/buscar/{matricula}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable UUID id) {
        Optional<Veiculo> veiculo = veiculoModel.buscarPorId(id);
        return veiculo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // PUT - Atualizar veículo
    @PutMapping("/atualizar/{matricula}")
    public ResponseEntity<Veiculo> atualizar(@PathVariable UUID id, @RequestBody Veiculo dados) {
        Veiculo atualizado = veiculoModel.atualizarVeiculo(id, dados);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(atualizado);
    }

    // DELETE - Remover veículo
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Optional<Veiculo> veiculo = veiculoModel.buscarPorId(id);
        if (veiculo.isPresent()) {
            veiculoModel.deletarVeiculo(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

