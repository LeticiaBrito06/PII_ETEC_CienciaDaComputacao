package com.labquest.backend.service;

import com.labquest.backend.dtos.user.UserRequest;
import com.labquest.backend.dtos.user.UserResponse;
import com.labquest.backend.dtos.user.UserUpdateRequest;
import com.labquest.backend.entity.PerformanceEntity;
import com.labquest.backend.entity.UserEntity;
import com.labquest.backend.entity.enums.DifficultyLevel;
import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.repository.PerformanceRepository;
import com.labquest.backend.repository.UserRepository;
import com.labquest.backend.util.Sha256PasswordService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final Sha256PasswordService passwordService;
    private final AuthService authService;

    public UserService(UserRepository userRepository, PerformanceRepository performanceRepository,
            Sha256PasswordService passwordService, AuthService authService) {
        this.userRepository = userRepository;
        this.performanceRepository = performanceRepository;
        this.passwordService = passwordService;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listAll() {
        return userRepository.findAll().stream().map(authService::mapUser).toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listByType(UserType tipo) {
        return userRepository.findByTipoAndAtivoTrueOrderByNomeAsc(tipo).stream().map(authService::mapUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserEntity> listEntitiesByType(UserType tipo) {
        return userRepository.findByTipoAndAtivoTrueOrderByNomeAsc(tipo);
    }

    @Transactional(readOnly = true)
    public UserEntity getEntityById(Integer id) {
        return userRepository.findById(id)
                .filter(UserEntity::isAtivo)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario nao encontrado."));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Integer id) {
        return authService.mapUser(getEntityById(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        userRepository.findByEmailIgnoreCaseAndAtivoTrue(request.email().trim().toLowerCase())
                .ifPresent(user -> {
                    throw new ApiException(HttpStatus.CONFLICT, "Ja existe um usuario ativo com este e-mail.");
                });

        UserEntity user = new UserEntity();
        user.setNome(request.nome().trim());
        user.setEmail(request.email().trim().toLowerCase());
        String salt = passwordService.generateSalt();
        user.setSalt(salt);
        user.setSenha(passwordService.hash(request.senha(), salt));
        user.setTipo(request.tipo());
        user.setAtivo(true);
        UserEntity savedUser = userRepository.save(user);

        if (savedUser.getTipo() == UserType.ALUNO) {
            PerformanceEntity performance = new PerformanceEntity();
            performance.setAluno(savedUser);
            performance.setDifficultyLevel(DifficultyLevel.FACIL);
            performanceRepository.save(performance);
        }

        return authService.mapUser(savedUser);
    }

    @Transactional
    public UserResponse update(Integer id, UserUpdateRequest request) {
        UserEntity user = getEntityById(id);

        userRepository.findByEmailIgnoreCaseAndAtivoTrue(request.email().trim().toLowerCase())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ApiException(HttpStatus.CONFLICT, "Ja existe um usuario ativo com este e-mail.");
                });

        user.setNome(request.nome().trim());
        user.setEmail(request.email().trim().toLowerCase());
        return authService.mapUser(userRepository.save(user));
    }

    @Transactional
    public void deactivate(Integer id) {
        UserEntity user = getEntityById(id);
        user.setAtivo(false);
        userRepository.save(user);
    }
}
