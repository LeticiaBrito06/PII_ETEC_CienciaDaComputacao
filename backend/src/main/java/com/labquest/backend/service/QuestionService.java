package com.labquest.backend.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.labquest.backend.config.LabQuestProperties;
import com.labquest.backend.dtos.question.AlternativeResponse;
import com.labquest.backend.dtos.question.QuestionRequest;
import com.labquest.backend.dtos.question.QuestionResponse;
import com.labquest.backend.entity.AlternativeEntity;
import com.labquest.backend.entity.QuestionEntity;
import com.labquest.backend.entity.UserEntity;
import com.labquest.backend.entity.enums.QuestionType;
import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.repository.QuestionRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AuthService authService;
    private final UserService userService;
    private final LabQuestProperties properties;

    public QuestionService(
            QuestionRepository questionRepository,
            AuthService authService,
            UserService userService,
            LabQuestProperties properties) {
        this.questionRepository = questionRepository;
        this.authService = authService;
        this.userService = userService;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> listAllActive() {
        return questionRepository
                .findByAtivoTrueOrderByDifficultyLevelAscIdAsc()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> listByType(QuestionType questionType) {
        return questionRepository
                .findByQuestionTypeAndAtivoTrueOrderByIdDesc(questionType)
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> listByProfessorAndType(Integer professorId, QuestionType questionType) {
        return questionRepository
                .findByProfessorIdAndQuestionTypeAndAtivoTrueOrderByIdDesc(professorId, questionType)
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionEntity getEntity(Integer id) {
        return questionRepository.findById(id)
                .filter(QuestionEntity::isAtivo)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Questao nao encontrada."));
    }

    @Transactional(readOnly = true)
    public QuestionResponse getById(Integer id) {
        return map(getEntity(id));
    }

    @Transactional
    public QuestionResponse create(Integer professorId, QuestionRequest request) {
        UserEntity professor = userService.getEntityById(professorId);

        if (professor.getTipo() != UserType.PROFESSOR) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Apenas professores podem cadastrar questoes.");
        }

        QuestionEntity question = new QuestionEntity();
        applyRequest(question, request, professor);

        return map(questionRepository.save(question));
    }

    @Transactional
    public QuestionResponse update(Integer questionId, QuestionRequest request) {
        QuestionEntity previousQuestion = getEntity(questionId);
        previousQuestion.setAtivo(false);

        QuestionEntity newQuestion = new QuestionEntity();
        applyRequest(newQuestion, request, previousQuestion.getProfessor());

        return map(questionRepository.save(newQuestion));
    }

    @Transactional
    public void deactivate(Integer questionId) {
        QuestionEntity question = getEntity(questionId);
        question.setAtivo(false);
        questionRepository.save(question);
    }

    private void applyRequest(QuestionEntity question, QuestionRequest request, UserEntity professor) {
        if (request.tipo() == QuestionType.MULTIPLA_ESCOLHA) {
            long correctCount = request.alternativas()
                    .stream()
                    .filter(alt -> alt.correta())
                    .count();

            if (correctCount != 1) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Questoes de multipla escolha devem ter exatamente uma alternativa correta.");
            }
        }

        question.setTexto(request.enunciado().trim());
        question.setQuestionType(request.tipo());
        question.setDifficultyLevel(request.nivelDificuldade());
        question.setCategoria(
                request.categoria() == null || request.categoria().isBlank()
                        ? "Materiais de laboratorio"
                        : request.categoria().trim());
        question.setImagemUrl(normalizeImagePathForStorage(request.imagemUrl()));
        question.setProfessor(professor);
        question.setAtivo(true);

        question.getAlternativas().clear();

        request.alternativas().forEach(item -> {
            AlternativeEntity alternative = new AlternativeEntity();

            alternative.setQuestao(question);
            alternative.setTexto(item.texto().trim());
            alternative.setImagemUrl(normalizeImagePathForStorage(item.imagemUrl()));
            alternative.setCorreta(
                    request.tipo() == QuestionType.ASSOCIACAO || item.correta());

            question.getAlternativas().add(alternative);
        });
    }

    public QuestionResponse map(QuestionEntity question) {
        return new QuestionResponse(
                question.getId(),
                question.getTexto(),
                question.getQuestionType(),
                question.getDifficultyLevel(),
                question.getCategoria(),
                convertToUrl(question.getImagemUrl()),
                question.isAtivo(),
                question.getCriadoEm(),
                authService.mapUser(question.getProfessor()),
                question.getAlternativas()
                        .stream()
                        .map(alt -> new AlternativeResponse(
                                alt.getId(),
                                alt.getTexto(),
                                convertToUrl(alt.getImagemUrl()),
                                alt.isCorreta()))
                        .toList());
    }

    private String convertToUrl(String imagemUrl) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return null;
        }
        // Se já é uma URL (começa com http ou https), retorna como está
        String normalizedPath = imagemUrl.trim().replaceAll("\\\\", "/");

        if ((normalizedPath.startsWith("http://") || normalizedPath.startsWith("https://"))
                && !normalizedPath.contains("/uploads/")
                && !normalizedPath.contains("imagens/")) {
            return normalizedPath;
        }

        // Se for um caminho absoluto do Windows (contém C:\ ou C:/), extrai a parte
        // relativa
        if (normalizedPath.contains("/uploads/")) {
            normalizedPath = normalizedPath.substring(normalizedPath.indexOf("/uploads/") + "/uploads/".length());
        } else if (normalizedPath.startsWith("uploads/")) {
            normalizedPath = normalizedPath.substring("uploads/".length());
        } else if (normalizedPath.contains("imagens/")) {
            normalizedPath = normalizedPath.substring(normalizedPath.indexOf("imagens/"));
        }

        // Remove barras iniciais
        normalizedPath = decodePath(normalizedPath.replaceAll("^/+", ""));

        // Constrói a URL completa
        return properties.publicBaseUrl() + "/uploads/" + encodePath(normalizedPath);
    }

    private String encodePath(String path) {
        return List.of(path.split("/"))
                .stream()
                .map(segment -> UriUtils.encodePathSegment(segment, StandardCharsets.UTF_8))
                .reduce((left, right) -> left + "/" + right)
                .orElse("");
    }

    private String normalizeImagePathForStorage(String imagemUrl) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return null;
        }

        String normalizedPath = imagemUrl.trim().replaceAll("\\\\", "/");

        int uploadsIndex = normalizedPath.indexOf("/uploads/");
        if (uploadsIndex >= 0) {
            normalizedPath = normalizedPath.substring(uploadsIndex + "/uploads/".length());
        }

        if (normalizedPath.startsWith("uploads/")) {
            normalizedPath = normalizedPath.substring("uploads/".length());
        }

        if (normalizedPath.contains("imagens/")) {
            normalizedPath = normalizedPath.substring(normalizedPath.indexOf("imagens/"));
        }

        return decodePath(normalizedPath.replaceAll("^/+", ""));
    }

    private String decodePath(String path) {
        try {
            return UriUtils.decode(path, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return path;
        }
    }
}
