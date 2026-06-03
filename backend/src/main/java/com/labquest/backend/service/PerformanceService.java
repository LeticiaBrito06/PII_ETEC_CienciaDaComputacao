package com.labquest.backend.service;

import com.labquest.backend.dtos.performance.PerformanceResponse;
import com.labquest.backend.entity.AnswerEntity;
import com.labquest.backend.entity.GameEntity;
import com.labquest.backend.entity.PerformanceEntity;
import com.labquest.backend.entity.UserEntity;
import com.labquest.backend.entity.enums.DifficultyLevel;
import com.labquest.backend.entity.enums.UserType;
import com.labquest.backend.exception.ApiException;
import com.labquest.backend.repository.GameRepository;
import com.labquest.backend.repository.PerformanceRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final GameRepository gameRepository;
    private final AuthService authService;
    private final UserService userService;

    public PerformanceService(PerformanceRepository performanceRepository, GameRepository gameRepository,
            AuthService authService, UserService userService) {
        this.performanceRepository = performanceRepository;
        this.gameRepository = gameRepository;
        this.authService = authService;
        this.userService = userService;
    }

    @Transactional
    public PerformanceEntity refreshForStudent(UserEntity aluno) {
        if (aluno.getTipo() != UserType.ALUNO) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Desempenho so existe para alunos.");
        }

        List<GameEntity> games = gameRepository.findByAlunoIdOrderByDataHoraInicioDescIdDesc(aluno.getId()).stream()
                .filter(GameEntity::isFinalizada)
                .toList();

        int totalPartidas = games.size();
        int totalAcertos = 0;
        int totalErros = 0;

        for (GameEntity game : games) {
            for (AnswerEntity answer : game.getRespostas()) {
                if (answer.isCorreta()) {
                    totalAcertos++;
                } else {
                    totalErros++;
                }
            }
        }

        double percentual = totalAcertos + totalErros == 0 ? 0.0 : (totalAcertos * 100.0) / (totalAcertos + totalErros);
        DifficultyLevel nivelMedio = percentual >= 70.0 ? DifficultyLevel.DIFICIL
                : percentual >= 40.0 ? DifficultyLevel.MEDIO : DifficultyLevel.FACIL;

        PerformanceEntity performance = performanceRepository.findByAlunoId(aluno.getId()).orElseGet(() -> {
            PerformanceEntity entity = new PerformanceEntity();
            entity.setAluno(aluno);
            return entity;
        });

        performance.setTotalPartidas(totalPartidas);
        performance.setTotalAcertos(totalAcertos);
        performance.setTotalErros(totalErros);
        performance.setPercentualAcerto(percentual);
        performance.setDifficultyLevel(nivelMedio);
        return performanceRepository.save(performance);
    }

    @Transactional(readOnly = true)
    public PerformanceResponse getByStudentId(Integer alunoId) {
        PerformanceEntity entity = performanceRepository.findByAlunoId(alunoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Desempenho do aluno nao encontrado."));
        return map(entity);
    }

    @Transactional(readOnly = true)
    public List<PerformanceResponse> listAll() {
        return performanceRepository.findAllByOrderByPercentualAcertoDesc().stream().map(this::map).toList();
    }

    @Transactional
    public PerformanceResponse refreshAndGet(Integer alunoId) {
        return map(refreshForStudent(userService.getEntityById(alunoId)));
    }

    public PerformanceResponse map(PerformanceEntity entity) {
        return new PerformanceResponse(
                entity.getId(),
                authService.mapUser(entity.getAluno()),
                entity.getTotalPartidas(),
                entity.getTotalAcertos(),
                entity.getTotalErros(),
                entity.getPercentualAcerto(),
                entity.getDifficultyLevel(),
                entity.getAtualizadoEm());
    }
}
