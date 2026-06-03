package com.labquest.backend.dtos.game;

import jakarta.validation.constraints.NotNull;

public record AnswerRequest(
        @NotNull Integer questionId,
        Integer alternativaId,
        boolean correta,
        Integer tempoResposta) {
}
