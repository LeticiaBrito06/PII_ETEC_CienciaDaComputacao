package com.labquest.backend.repository;

import com.labquest.backend.entity.PerformanceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<PerformanceEntity, Integer> {

    @EntityGraph(attributePaths = "aluno")
    Optional<PerformanceEntity> findByAlunoId(Integer alunoId);

    @EntityGraph(attributePaths = "aluno")
    List<PerformanceEntity> findAllByOrderByPercentualAcertoDesc();
}
