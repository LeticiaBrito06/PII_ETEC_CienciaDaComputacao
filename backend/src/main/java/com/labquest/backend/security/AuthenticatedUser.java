package com.labquest.backend.security;

import com.labquest.backend.entity.enums.UserType;

public record AuthenticatedUser(Integer id, String email, String nome, UserType tipo) {
}
