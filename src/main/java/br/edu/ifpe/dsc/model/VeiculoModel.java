package br.edu.ifpe.dsc.model;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.dsc.model.dto.Checklist;
import br.edu.ifpe.dsc.model.dto.Veiculo;
import br.edu.ifpe.dsc.model.repositorios.ChecklistRepositorio;
import br.edu.ifpe.dsc.model.repositorios.VeiculoRepositorio;

@Component
public class VeiculoModel {

    private final VeiculoRepositorio veiculoRepositorio;
    private final ChecklistRepositorio checklistRepositorio;

    public VeiculoModel(
            VeiculoRepositorio veiculoRepositorio,
            ChecklistRepositorio checklistRepositorio) {

        this.veiculoRepositorio = veiculoRepositorio;
        this.checklistRepositorio = checklistRepositorio;
    }

    // CADASTRAR
    public Veiculo salvar(Veiculo veiculo) {

        normalizarDados(veiculo);

        if (veiculoRepositorio.existsByNumero(veiculo.getNumero())) {
            throw new IllegalArgumentException(
                    "Número do veículo já cadastrado."
            );
        }

        if (veiculoRepositorio
                .findByPlaca(veiculo.getPlaca())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Placa já cadastrada."
            );
        }

        return veiculoRepositorio.save(veiculo);
    }

    // LISTAR
    public List<Veiculo> listarTodos() {
        return veiculoRepositorio.findAll();
    }

    // BUSCAR
    public Optional<Veiculo> buscarPorNumero(long numero) {
        return veiculoRepositorio.findByNumero(numero);
    }

    // EXCLUIR
    @Transactional
    public void deletarVeiculo(Veiculo veiculo) {

        List<Checklist> vinculados =
                checklistRepositorio
                        .findByVeiculoOrderByCriadoEmDesc(veiculo);

        vinculados.forEach(checklist ->
                checklist.setVeiculo(null)
        );

        checklistRepositorio.saveAll(vinculados);
        veiculoRepositorio.delete(veiculo);
    }

    // ATUALIZAR
    @Transactional
    public Veiculo atualizar(
            long numeroOriginal,
            Veiculo dados) {

        Veiculo veiculoAtual = veiculoRepositorio
                .findByNumero(numeroOriginal)
                .orElse(null);

        if (veiculoAtual == null) {
            return null;
        }

        normalizarDados(dados);

        /*
         * Verifica se o novo número está sendo utilizado
         * por outro veículo.
         */
        Optional<Veiculo> veiculoComMesmoNumero =
                veiculoRepositorio.findByNumero(
                        dados.getNumero()
                );

        if (veiculoComMesmoNumero.isPresent()
                && !veiculoComMesmoNumero
                        .get()
                        .getId()
                        .equals(veiculoAtual.getId())) {

            throw new IllegalArgumentException(
                    "Número do veículo já cadastrado."
            );
        }

        /*
         * Verifica se a placa está sendo utilizada
         * por outro veículo.
         *
         * A placa do próprio veículo é permitida.
         */
        Optional<Veiculo> veiculoComMesmaPlaca =
                veiculoRepositorio.findByPlaca(
                        dados.getPlaca()
                );

        if (veiculoComMesmaPlaca.isPresent()
                && !veiculoComMesmaPlaca
                        .get()
                        .getId()
                        .equals(veiculoAtual.getId())) {

            throw new IllegalArgumentException(
                    "Placa já cadastrada em outro veículo."
            );
        }

        veiculoAtual.setNumero(dados.getNumero());
        veiculoAtual.setPlaca(dados.getPlaca());
        veiculoAtual.setMarca(dados.getMarca());
        veiculoAtual.setTipo(dados.getTipo());

        return veiculoRepositorio.save(veiculoAtual);
    }

    private void normalizarDados(Veiculo veiculo) {

        if (veiculo.getPlaca() != null) {
            veiculo.setPlaca(
                    veiculo.getPlaca()
                            .trim()
                            .toUpperCase()
            );
        }
    }
}