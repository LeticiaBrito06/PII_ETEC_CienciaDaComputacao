package com.labquest.backend.dtos.question;

import java.time.LocalDateTime;
import java.util.List;

import com.labquest.backend.dtos.user.UserResponse;
import com.labquest.backend.entity.enums.DifficultyLevel;
import com.labquest.backend.entity.enums.QuestionType;

public record QuestionResponse(
        Integer id,
        String enunciado,
        QuestionType tipo,
        DifficultyLevel nivelDificuldade,
        String categoria,
        String imagemUrl,
        boolean ativa,
        LocalDateTime criadaEm,
        UserResponse professor,
        List<AlternativeResponse> alternativas) {
}
