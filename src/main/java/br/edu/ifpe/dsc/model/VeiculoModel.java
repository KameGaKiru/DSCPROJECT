package br.edu.ifpe.dsc.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.edu.ifpe.dsc.model.dto.Checklist;
import br.edu.ifpe.dsc.model.dto.Veiculo;
import br.edu.ifpe.dsc.model.repositorios.ChecklistRepositorio;
import br.edu.ifpe.dsc.model.repositorios.VeiculoRepositorio;

@Component
public class VeiculoModel {

    @Autowired
    private VeiculoRepositorio veiculoRepositorio;

    @Autowired
    private ChecklistRepositorio checklistRepositorio;

        // CADASTRAR
    public Veiculo salvar(Veiculo veiculo) {

        if (veiculoRepositorio.existsByNumero(veiculo.getNumero())) {
            throw new IllegalArgumentException("Número do veículo já cadastrado.");
        }

        if (veiculoRepositorio.findByPlaca(veiculo.getPlaca()).isPresent()) {
            throw new IllegalArgumentException("Placa já cadastrada.");
        }

        return veiculoRepositorio.save(veiculo);
    }

    // LISTAR
    public List<Veiculo> listarTodos() {
        return veiculoRepositorio.findAll();
    }

    // BUSCAR
    public Optional<Veiculo> buscarPorNumero(Integer numero) {
        return veiculoRepositorio.findByNumero(numero);
    }

    // EXCLUIR
    public void deletarVeiculo(Veiculo veiculo) {
        
        List<Checklist> vinculados = checklistRepositorio
                .findByVeiculoOrderByCriadoEmDesc(veiculo);

        vinculados.forEach(c -> c.setVeiculo(null));
        checklistRepositorio.saveAll(vinculados);

        veiculoRepositorio.delete(veiculo);
    }

    // ATUALIZAR
    public Veiculo atualizar(Integer numeroOriginal, Veiculo dados) {

        return veiculoRepositorio.findByNumero(numeroOriginal)
            .map(veiculo -> {

                if (dados.getNumero() != numeroOriginal) {
                    if (veiculoRepositorio.findByNumero(dados.getNumero()).isPresent()) {
                        throw new IllegalArgumentException("Número do veículo já cadastrado.");
                    }
                    veiculo.setNumero(dados.getNumero());
                }

                Optional<Veiculo> placaExistente =
                        veiculoRepositorio.findByPlaca(dados.getPlaca());
                if (placaExistente.isPresent() &&
                    !numeroOriginal.equals(placaExistente.get().getNumero())) {
                    throw new IllegalArgumentException("Placa já cadastrada.");
                }

                veiculo.setPlaca(dados.getPlaca().trim().toUpperCase());
                veiculo.setMarca(dados.getMarca());
                veiculo.setTipo(dados.getTipo());

                return veiculoRepositorio.save(veiculo);
            }).orElse(null);
    }

}