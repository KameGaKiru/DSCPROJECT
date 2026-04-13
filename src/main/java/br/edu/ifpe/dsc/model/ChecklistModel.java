package br.edu.ifpe.dsc.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifpe.dsc.model.dto.Checklist;
import br.edu.ifpe.dsc.model.dto.Usuario;
import br.edu.ifpe.dsc.model.dto.Veiculo;
import br.edu.ifpe.dsc.model.repositorios.ChecklistRepositorio;
import br.edu.ifpe.dsc.model.repositorios.UsuarioRepositorio;
import br.edu.ifpe.dsc.model.repositorios.VeiculoRepositorio;

@Service
public class ChecklistModel {

    @Autowired
    private ChecklistRepositorio checklistRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private VeiculoRepositorio veiculoRepositorio;

    // SALVAR
    public Checklist salvarChecklist(String matricula, int numeroVeiculo, Checklist checklist) {

        Usuario motorista = usuarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado: " + matricula));

        Veiculo veiculo = veiculoRepositorio.findByNumero(numeroVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + numeroVeiculo));

        validar(checklist);

        checklist.setMotorista(motorista);
        checklist.setVeiculo(veiculo);

        return checklistRepositorio.save(checklist);
    }

    // LISTAR TODOS
    public List<Checklist> listarTodos() {
        return checklistRepositorio.findAllByOrderByCriadoEmDesc();
    }

    // LISTAR POR MOTORISTA
    public List<Checklist> listarPorMotorista(String matricula) {
        Usuario motorista = usuarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado: " + matricula));
        return checklistRepositorio.findByMotoristaOrderByCriadoEmDesc(motorista);
    }

    // LISTAR POR VEÍCULO
    public List<Checklist> listarPorVeiculo(int numeroVeiculo) {
        Veiculo veiculo = veiculoRepositorio.findByNumero(numeroVeiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + numeroVeiculo));
        return checklistRepositorio.findByVeiculoOrderByCriadoEmDesc(veiculo);
    }

    // VALIDAÇÃO
    private void validar(Checklist c) {

        if (c.getTipo() == null || c.getTipo().isBlank())
            throw new IllegalArgumentException("Tipo obrigatório (ENTRADA ou SAIDA)");

        if (!c.getTipo().equalsIgnoreCase("ENTRADA") &&
            !c.getTipo().equalsIgnoreCase("SAIDA"))
            throw new IllegalArgumentException("Tipo inválido. Use ENTRADA ou SAIDA");

        if (c.getKm() < 0)
            throw new IllegalArgumentException("KM não pode ser negativo");
    }
}