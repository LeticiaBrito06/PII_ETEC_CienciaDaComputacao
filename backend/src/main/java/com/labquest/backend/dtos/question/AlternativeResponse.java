package com.labquest.backend.dtos.question;

public record AlternativeResponse(
        Integer id,
        String texto,
        String imagemUrl,
        boolean correta) {
}
