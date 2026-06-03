package com.labquest.backend.dtos.performance;

import com.labquest.backend.dtos.user.UserResponse;
import com.labquest.backend.entity.enums.DifficultyLevel;
import java.time.LocalDateTime;

public record PerformanceResponse(
        Integer id,
        UserResponse aluno,
        int totalPartidas,
        int totalAcertos,
        int totalErros,
        double percentualAcerto,
        DifficultyLevel nivelMedio,
        LocalDateTime atualizadoEm) {
}
