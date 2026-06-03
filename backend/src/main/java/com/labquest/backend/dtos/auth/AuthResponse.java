package com.labquest.backend.dtos.auth;

import com.labquest.backend.dtos.user.UserResponse;

public record AuthResponse(String token, UserResponse usuario) {
}
