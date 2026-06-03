package com.labquest.backend.repository;

import com.labquest.backend.entity.GameEntity;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Integer> {

    @EntityGraph(attributePaths = {"aluno", "respostas", "respostas.questao", "respostas.alternativa"})
    List<GameEntity> findByAlunoIdOrderByDataHoraInicioDescIdDesc(Integer alunoId);
}
