package com.labquest.backend.controller;

import com.labquest.backend.dtos.question.QuestionRequest;
import com.labquest.backend.dtos.question.QuestionResponse;
import com.labquest.backend.entity.enums.QuestionType;
import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.service.CurrentUserService;
import com.labquest.backend.service.QuestionService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final CurrentUserService currentUserService;

    public QuestionController(QuestionService questionService, CurrentUserService currentUserService) {
        this.questionService = questionService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<QuestionResponse> listAll(@RequestParam(required = false) QuestionType tipo,
            @RequestParam(required = false) Integer professorId) {
        if (professorId != null && tipo != null) {
            return questionService.listByProfessorAndType(professorId, tipo);
        }
        if (tipo != null) {
            return questionService.listByType(tipo);
        }
        return questionService.listAllActive();
    }

    @GetMapping("/{id}")
    public QuestionResponse getById(@PathVariable Integer id) {
        return questionService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse create(@Valid @RequestBody QuestionRequest request) {
        currentUserService.requireRole(UserType.PROFESSOR);
        return questionService.create(currentUserService.getCurrentUser().id(), request);
    }

    @PutMapping("/{id}")
    public QuestionResponse update(@PathVariable Integer id, @Valid @RequestBody QuestionRequest request) {
        currentUserService.requireRole(UserType.PROFESSOR);
        return questionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        currentUserService.requireRole(UserType.PROFESSOR);
        questionService.deactivate(id);
    }
}
