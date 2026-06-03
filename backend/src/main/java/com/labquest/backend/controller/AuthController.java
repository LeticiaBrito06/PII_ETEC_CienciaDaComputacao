package com.labquest.backend.controller;

import com.labquest.backend.dtos.auth.AuthResponse;
import com.labquest.backend.dtos.auth.LoginRequest;
import com.labquest.backend.dtos.user.UserResponse;
import com.labquest.backend.service.AuthService;
import com.labquest.backend.service.CurrentUserService;
import com.labquest.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final UserService userService;

    public AuthController(AuthService authService, CurrentUserService currentUserService, UserService userService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me() {
        return userService.getById(currentUserService.getCurrentUser().id());
    }
}
