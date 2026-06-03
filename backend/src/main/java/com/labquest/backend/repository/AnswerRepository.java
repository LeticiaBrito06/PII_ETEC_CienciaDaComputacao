package com.labquest.backend.repository;

import com.labquest.backend.entity.AnswerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Integer> {

    @EntityGraph(attributePaths = {"questao", "alternativa", "partida"})
    List<AnswerEntity> findByGameIdOrderByIdAsc(Integer partidaId);
}
