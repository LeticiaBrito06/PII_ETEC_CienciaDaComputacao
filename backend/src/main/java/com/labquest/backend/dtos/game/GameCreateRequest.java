package com.labquest.backend.dtos.game;

import com.labquest.backend.entity.enums.DifficultyLevel;
import jakarta.validation.constraints.NotNull;

public record GameCreateRequest(@NotNull Integer studentId, DifficultyLevel difficultyLevel) {
}
