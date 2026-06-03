package com.labquest.backend.controller;

import com.labquest.backend.dtos.user.UserRequest;
import com.labquest.backend.dtos.user.UserResponse;
import com.labquest.backend.dtos.user.UserUpdateRequest;
import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.service.CurrentUserService;
import com.labquest.backend.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public UserController(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<UserResponse> listAll() {
        currentUserService.requireRole(UserType.PROFESSOR);
        return userService.listAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/alunos")
    public List<UserResponse> listStudents() {
        return userService.listByType(UserType.ALUNO);
    }

    @GetMapping("/professores")
    public List<UserResponse> listTeachers() {
        currentUserService.requireRole(UserType.PROFESSOR);
        return userService.listByType(UserType.PROFESSOR);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        currentUserService.requireRole(UserType.PROFESSOR);
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Integer id) {
        currentUserService.requireRole(UserType.PROFESSOR);
        userService.deactivate(id);
    }
}
