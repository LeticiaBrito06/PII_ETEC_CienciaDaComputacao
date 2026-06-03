package com.labquest.backend.dtos.user;

import com.labquest.backend.entity.enums.UserType;
import java.time.LocalDateTime;

public record UserResponse(
        Integer id,
        String nome,
        String email,
        UserType tipo,
        boolean ativo,
        LocalDateTime criadoEm) {
}
