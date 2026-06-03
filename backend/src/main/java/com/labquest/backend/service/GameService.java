package com.labquest.backend.service;

import com.labquest.backend.dtos.game.AnswerRequest;
import com.labquest.backend.dtos.game.AnswerResponse;
import com.labquest.backend.dtos.game.GameCreateRequest;
import com.labquest.backend.dtos.game.GameFinishRequest;
import com.labquest.backend.dtos.game.GameResponse;
import com.labquest.backend.dtos.question.AlternativeResponse;
import com.labquest.backend.dtos.question.QuestionResponse;
import com.labquest.backend.entity.AlternativeEntity;
import com.labquest.backend.entity.AnswerEntity;
import com.labquest.backend.entity.GameEntity;
import com.labquest.backend.entity.QuestionEntity;
import com.labquest.backend.entity.UserEntity;
import com.labquest.backend.entity.enums.DifficultyLevel;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.repository.AlternativeRepository;
import com.labquest.backend.repository.GameRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final AlternativeRepository alternativeRepository;
    private final UserService userService;
    private final QuestionService questionService;
    private final PerformanceService performanceService;
    private final AuthService authService;

    public GameService(GameRepository gameRepository, AlternativeRepository alternativeRepository,
            UserService userService, QuestionService questionService, PerformanceService performanceService,
            AuthService authService) {
        this.gameRepository = gameRepository;
        this.alternativeRepository = alternativeRepository;
        this.userService = userService;
        this.questionService = questionService;
        this.performanceService = performanceService;
        this.authService = authService;
    }

    @Transactional
    public GameResponse start(GameCreateRequest request) {
        UserEntity aluno = userService.getEntityById(request.studentId());
        GameEntity game = new GameEntity();
        game.setAluno(aluno);
        game.setDataHoraInicio(LocalDateTime.now());
        game.setDifficultyLevel(request.difficultyLevel() == null ? DifficultyLevel.FACIL : request.difficultyLevel());
        game.setFinalizada(false);
        return map(gameRepository.save(game));
    }

    @Transactional
    public GameResponse finish(Integer gameId, GameFinishRequest request) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Partida nao encontrada."));

        if (!game.getAluno().getId().equals(request.studentId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A partida nao pertence ao aluno informado.");
        }

        game.getRespostas().clear();
        for (AnswerRequest item : request.respostas()) {
            QuestionEntity question = questionService.getEntity(item.questionId());
            AlternativeEntity alternative = item.alternativaId() == null ? null
                    : alternativeRepository.findById(item.alternativaId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Alternativa nao encontrada."));

            AnswerEntity answer = new AnswerEntity();
            answer.setGame(game);
            answer.setQuestao(question);
            answer.setAlternativa(alternative);
            boolean correta = alternative != null ? alternative.isCorreta() : item.correta();
            answer.setCorreta(correta);
            answer.setTempoResposta(item.tempoResposta() == null ? 0 : item.tempoResposta());
            game.getRespostas().add(answer);
        }

        game.setPontuacao(calculateScore(game.getRespostas()));
        game.setDifficultyLevel(
                request.difficultyLevel() == null ? game.getDifficultyLevel() : request.difficultyLevel());
        game.setFinalizada(true);
        game.setDataHoraFim(LocalDateTime.now());
        GameEntity saved = gameRepository.save(game);
        performanceService.refreshForStudent(saved.getAluno());
        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<GameResponse> listByStudent(Integer alunoId) {
        return gameRepository.findByAlunoIdOrderByDataHoraInicioDescIdDesc(alunoId).stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public GameResponse getById(Integer id) {
        return map(gameRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Partida nao encontrada.")));
    }

    private int calculateScore(List<AnswerEntity> answers) {
        int total = 0;
        for (AnswerEntity answer : answers) {
            if (answer.isCorreta()) {
                total += switch (answer.getQuestao().getDifficultyLevel()) {
                    case FACIL -> 10;
                    case MEDIO -> 20;
                    case DIFICIL -> 30;
                };
            }
        }
        return total;
    }

    public GameResponse map(GameEntity game) {
        return new GameResponse(
                game.getId(),
                authService.mapUser(game.getAluno()),
                game.getDataHoraInicio(),
                game.getDataHoraFim(),
                game.getPontuacao(),
                game.getDifficultyLevel(),
                game.isFinalizada(),
                game.getRespostas().stream().map(this::mapAnswer).toList());
    }

    private AnswerResponse mapAnswer(AnswerEntity answer) {
        QuestionResponse question = questionService.map(answer.getQuestao());
        AlternativeResponse alternative = answer.getAlternativa() == null
                ? null
                : new AlternativeResponse(
                        answer.getAlternativa().getId(),
                        answer.getAlternativa().getTexto(),
                        answer.getAlternativa().getImagemUrl(),
                        answer.getAlternativa().isCorreta());
        return new AnswerResponse(answer.getId(), question, alternative, answer.isCorreta(), answer.getTempoResposta());
    }
}
