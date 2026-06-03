package com.labquest.backend.service;

import com.labquest.backend.dtos.auth.AuthResponse;
import com.labquest.backend.dtos.auth.LoginRequest;
import com.labquest.backend.dtos.user.UserResponse;
import com.labquest.backend.entity.UserEntity;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.repository.UserRepository;
import com.labquest.backend.security.JwtService;
import com.labquest.backend.util.Sha256PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final Sha256PasswordService passwordService;

    public AuthService(UserRepository userRepository, JwtService jwtService, Sha256PasswordService passwordService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmailIgnoreCaseAndAtivoTrue(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "E-mail institucional nao encontrado."));

        String hashedPassword = passwordService.hash(request.senha(), user.getSalt());
        if (!hashedPassword.equals(user.getSenha())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Senha incorreta.");
        }

        return new AuthResponse(jwtService.generateToken(user), mapUser(user));
    }

    public UserResponse mapUser(UserEntity user) {
        return new UserResponse(user.getId(), user.getNome(), user.getEmail(), user.getTipo(), user.isAtivo(),
                user.getCriadoEm());
    }
}
