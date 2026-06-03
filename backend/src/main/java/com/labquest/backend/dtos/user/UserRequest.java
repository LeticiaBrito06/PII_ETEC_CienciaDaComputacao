package com.labquest.backend.dtos.user;

import com.labquest.backend.entity.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
                @NotBlank String nome,
                @NotBlank @Email String email,
                @NotBlank @Pattern(regexp = "^\\d{11}$", message = "A senha deve conter 11 digitos numericos.") String senha,
                @NotNull UserType tipo) {
}
