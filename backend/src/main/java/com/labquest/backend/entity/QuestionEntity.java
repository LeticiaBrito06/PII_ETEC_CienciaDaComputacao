package com.labquest.backend.entity;

import com.labquest.backend.entity.enums.DifficultyLevel;
import com.labquest.backend.entity.enums.QuestionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "questao")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "enunciado", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_dificuldade", nullable = false)
    private DifficultyLevel difficultyLevel;

    @Column(length = 80)
    private String categoria;

    @Column(name = "imagem_url", length = 300)
    private String imagemUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_professor", nullable = false)
    private UserEntity professor;

    @Column(name = "ativa", nullable = false)
    private boolean ativo = true;

    @Column(name = "criada_em", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<AlternativeEntity> alternativas = new ArrayList<>();
}