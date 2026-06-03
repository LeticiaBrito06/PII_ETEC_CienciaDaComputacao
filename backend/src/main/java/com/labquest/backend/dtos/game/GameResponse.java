package com.labquest.backend.dtos.game;

import com.labquest.backend.dtos.user.UserResponse;
import com.labquest.backend.entity.enums.DifficultyLevel;
import java.time.LocalDateTime;
import java.util.List;

public record GameResponse(
        Integer id,
        UserResponse aluno,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        int pontuacao,
        DifficultyLevel nivelAtual,
        boolean finalizada,
        List<AnswerResponse> respostas) {
}
