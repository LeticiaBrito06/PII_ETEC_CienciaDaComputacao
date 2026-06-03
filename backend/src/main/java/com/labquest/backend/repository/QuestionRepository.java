package com.labquest.backend.repository;

import com.labquest.backend.entity.QuestionEntity;
import com.labquest.backend.entity.enums.QuestionType;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {

    @EntityGraph(attributePaths = { "alternativas", "professor" })
    List<QuestionEntity> findByAtivoTrueOrderByDifficultyLevelAscIdAsc();

    @EntityGraph(attributePaths = { "alternativas", "professor" })
    List<QuestionEntity> findByProfessorIdAndQuestionTypeAndAtivoTrueOrderByIdDesc(
            Integer professorId,
            QuestionType questionType);

    @EntityGraph(attributePaths = { "alternativas", "professor" })
    List<QuestionEntity> findByQuestionTypeAndAtivoTrueOrderByIdDesc(
            QuestionType questionType);

    @EntityGraph(attributePaths = { "alternativas", "professor" })
    List<QuestionEntity> findByAtivoTrueOrderByIdDesc();
}