package br.edu.ifpe.dsc.model;

import java.time.LocalDateTime;
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

    // SALVAR (motorista)
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

    // REGISTRAR SOLUÇÃO (mecânico)
    public Checklist registrarSolucao(Long checklistId, String matriculaMecanico, String solucao) {

        Checklist checklist = checklistRepositorio.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist não encontrado: " + checklistId));

        Usuario mecanico = usuarioRepositorio.findByMatricula(matriculaMecanico)
                .orElseThrow(() -> new IllegalArgumentException("Mecânico não encontrado: " + matriculaMecanico));

        if (solucao == null || solucao.isBlank())
            throw new IllegalArgumentException("A descrição da solução é obrigatória.");

        checklist.setSolucaoMecanico(solucao.trim());
        checklist.setMecanico(mecanico);
        checklist.setResolvidoEm(LocalDateTime.now());

        return checklistRepositorio.save(checklist);
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