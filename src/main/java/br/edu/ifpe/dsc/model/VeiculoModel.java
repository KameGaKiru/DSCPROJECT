package br.edu.ifpe.dsc.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.edu.ifpe.dsc.model.dto.Veiculo;
import br.edu.ifpe.dsc.model.repositorios.VeiculoRepositorio;

@Component
public class VeiculoModel {

    @Autowired
    private VeiculoRepositorio veiculoRepositorio;

    public Veiculo salvar(Veiculo veiculo) {
        return veiculoRepositorio.save(veiculo);
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepositorio.findAll();
    }

    public Optional<Veiculo> buscarPorNumero(Integer numero) {
        return veiculoRepositorio.findByNumero(numero);
    }

    public void deletarVeiculo(Veiculo veiculo) {
        veiculoRepositorio.delete(veiculo);
    }

    public Veiculo atualizar(Integer numero, Veiculo dados) {
        return veiculoRepositorio.findByNumero(numero)
            .map(veiculo -> {
                if (dados.getPlaca() != null && !dados.getPlaca().isBlank()) {
                    veiculo.setPlaca(dados.getPlaca());
                }
                if (dados.getMarca() != null) {
                    veiculo.setMarca(dados.getMarca());
                }
                if (dados.getTipo() != null) {
                    veiculo.setTipo(dados.getTipo());
                }
                return veiculoRepositorio.save(veiculo);
            })
            .orElse(null);
    }
}
