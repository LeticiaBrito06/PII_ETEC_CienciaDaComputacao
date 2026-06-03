package com.labquest.backend.dtos.question;

import jakarta.validation.constraints.NotBlank;

public record AlternativeRequest(
        Integer id,
        @NotBlank String texto,
        String imagemUrl,
        boolean correta) {
}
