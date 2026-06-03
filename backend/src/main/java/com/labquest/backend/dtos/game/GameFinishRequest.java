package com.labquest.backend.dtos.game;

import com.labquest.backend.entity.enums.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GameFinishRequest(
        @NotNull Integer studentId,
        @Valid @NotNull List<AnswerRequest> respostas,
        Integer pontuacao,
        DifficultyLevel difficultyLevel) {
}