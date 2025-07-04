package br.edu.ifpe.dsc.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.edu.ifpe.dsc.model.dto.Veiculo;
import br.edu.ifpe.dsc.model.repositorios.VeiculoRepositorio;

@Component
public class VeiculoModel {

    @Autowired
    private VeiculoRepositorio veiculoRepositorio;

    public Veiculo salvarVeiculo(Veiculo veiculo) {
        return veiculoRepositorio.save(veiculo);
    }

    public List<Veiculo> listarVeiculos() {
        return veiculoRepositorio.findAll();
    }

    public Optional<Veiculo> buscarPorId(UUID id) {
        return veiculoRepositorio.findById(id);
    }

    public void deletarVeiculo(UUID id) {
        veiculoRepositorio.deleteById(id);
    }

    public Veiculo atualizarVeiculo(UUID id, Veiculo dados) {
        return veiculoRepositorio.findById(id)
            .map(veiculo -> {
                veiculo.setPlaca(dados.getPlaca());
                veiculo.setModelo(dados.getModelo());
                veiculo.setTipo(dados.getTipo());
                veiculo.setNumero(dados.getNumero());
                return veiculoRepositorio.save(veiculo);
            }).orElse(null);
    }
}

