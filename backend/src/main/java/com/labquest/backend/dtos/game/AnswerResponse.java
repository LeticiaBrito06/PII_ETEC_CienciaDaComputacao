package com.labquest.backend.dtos.game;

import com.labquest.backend.dtos.question.AlternativeResponse;
import com.labquest.backend.dtos.question.QuestionResponse;

public record AnswerResponse(
        Integer id,
        QuestionResponse questao,
        AlternativeResponse alternativa,
        boolean correta,
        int tempoResposta) {
}
