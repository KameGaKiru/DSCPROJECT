package br.edu.ifpe.dsc.model.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpe.dsc.model.dto.Checklist;
import br.edu.ifpe.dsc.model.dto.Usuario;
import br.edu.ifpe.dsc.model.dto.Veiculo;

@Repository
public interface ChecklistRepositorio extends JpaRepository<Checklist, Long> {

    List<Checklist> findAllByOrderByCriadoEmDesc();

    List<Checklist> findByMotoristaOrderByCriadoEmDesc(Usuario motorista);

    List<Checklist> findByVeiculoOrderByCriadoEmDesc(Veiculo veiculo);
}