package com.labquest.backend.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank String nome,
        @NotBlank @Email String email) {
}
