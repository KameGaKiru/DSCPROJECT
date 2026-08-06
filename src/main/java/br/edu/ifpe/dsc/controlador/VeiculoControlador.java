package br.edu.ifpe.dsc.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ifpe.dsc.model.VeiculoModel;
import br.edu.ifpe.dsc.model.dto.Veiculo;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veiculo")
public class VeiculoControlador {

    private final VeiculoModel veiculoModel;

    public VeiculoControlador(
            VeiculoModel veiculoModel) {

        this.veiculoModel = veiculoModel;
    }

    // POST - CADASTRAR
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody Veiculo veiculo) {

        try {
            Veiculo salvo =
                    veiculoModel.salvar(veiculo);

            return ResponseEntity.ok(salvo);

        } catch (IllegalArgumentException erro) {
            return ResponseEntity
                    .badRequest()
                    .body(erro.getMessage());
        }
    }

    // GET - LISTAR TODOS
    @GetMapping("/listar")
    public List<Veiculo> listar() {
        return veiculoModel.listarTodos();
    }

    // GET - BUSCAR POR NÚMERO
    @GetMapping("/buscar/{numero}")
    public ResponseEntity<Veiculo> buscarPorNumero(
            @PathVariable long numero) {

        Optional<Veiculo> veiculo =
                veiculoModel.buscarPorNumero(numero);

        return veiculo
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // PUT - ATUALIZAR PELO NÚMERO ORIGINAL
    @PutMapping("/atualizar/{numeroOriginal}")
    public ResponseEntity<?> atualizar(
            @PathVariable long numeroOriginal,
            @Valid @RequestBody Veiculo dados) {

        try {
            Veiculo atualizado =
                    veiculoModel.atualizar(
                            numeroOriginal,
                            dados
                    );

            if (atualizado == null) {
                return ResponseEntity
                        .notFound()
                        .build();
            }

            return ResponseEntity.ok(atualizado);

        } catch (IllegalArgumentException erro) {
            return ResponseEntity
                    .badRequest()
                    .body(erro.getMessage());
        }
    }

    // DELETE - EXCLUIR PELO NÚMERO
    @DeleteMapping("/deletar/{numero}")
    public ResponseEntity<Void> deletar(
            @PathVariable long numero) {

        Optional<Veiculo> veiculo =
                veiculoModel.buscarPorNumero(numero);

        if (veiculo.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        veiculoModel.deletarVeiculo(
                veiculo.get()
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}