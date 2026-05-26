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

    // REGEX DE MODELO DE PLACA
    private static final String PLACA_REGEX =
            "^[A-Z]{3}-([0-9]{4}|[0-9][A-Z][0-9]{2})$";

    // CADASTRAR
    public Veiculo salvar(Veiculo veiculo) {
        validarVeiculo(veiculo);

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
        validarVeiculo(dados);

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

    // VALIDAÇÕES
    private void validarVeiculo(Veiculo veiculo) {

        if (veiculo.getNumero() <= 0) {
            throw new IllegalArgumentException(
                    "O número do veículo deve ser inteiro e positivo.");
        }

        // Máximo 10 dígitos
        if (String.valueOf(veiculo.getNumero()).length() > 10) {
            throw new IllegalArgumentException(
                    "O número do veículo deve ter no máximo 10 dígitos.");
        }

        if (veiculo.getPlaca() == null || veiculo.getPlaca().isBlank()) {
            throw new IllegalArgumentException("A placa é obrigatória.");
        }

        String placa = veiculo.getPlaca().trim().toUpperCase();

        if (!placa.matches(PLACA_REGEX)) {
            throw new IllegalArgumentException(
                    "Placa inválida. Utilize os formatos AAA-1234 ou AAA-1A23.");
        }

        veiculo.setPlaca(placa);

        if (veiculo.getMarca() == null) {
            throw new IllegalArgumentException("A marca é obrigatória.");
        }

        if (veiculo.getTipo() == null) {
            throw new IllegalArgumentException("O tipo é obrigatório.");
        }
    }
}