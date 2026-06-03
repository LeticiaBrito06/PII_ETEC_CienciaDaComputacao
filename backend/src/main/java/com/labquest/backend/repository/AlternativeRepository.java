package com.labquest.backend.repository;

import com.labquest.backend.entity.AlternativeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlternativeRepository extends JpaRepository<AlternativeEntity, Integer> {
    List<AlternativeEntity> findByQuestaoIdOrderByIdAsc(Integer questaoId);
}
