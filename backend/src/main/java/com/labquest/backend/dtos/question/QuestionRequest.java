package com.labquest.backend.dtos.question;

import java.util.List;

import com.labquest.backend.entity.enums.DifficultyLevel;
import com.labquest.backend.entity.enums.QuestionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionRequest(
                @NotBlank String enunciado,
                @NotNull QuestionType tipo,
                @NotNull DifficultyLevel nivelDificuldade,
                String categoria,
                String imagemUrl,
                @Valid @NotNull List<AlternativeRequest> alternativas) {
}
