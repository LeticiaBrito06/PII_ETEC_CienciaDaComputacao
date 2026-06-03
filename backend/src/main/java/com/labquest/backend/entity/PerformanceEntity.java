package com.labquest.backend.entity;

import com.labquest.backend.entity.enums.DifficultyLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "desempenho")
public class PerformanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluno", nullable = false, unique = true)
    private UserEntity aluno;

    @Column(name = "total_partidas", nullable = false)
    private int totalPartidas;

    @Column(name = "total_acertos", nullable = false)
    private int totalAcertos;

    @Column(name = "total_erros", nullable = false)
    private int totalErros;

    @Column(name = "percentual_acerto", nullable = false)
    private double percentualAcerto;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_medio", nullable = false)
    private DifficultyLevel difficultyLevel = DifficultyLevel.FACIL;

    @Column(name = "atualizado_em", nullable = false, insertable = false, updatable = false)
    private LocalDateTime atualizadoEm;
}
