package com.labquest.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resposta")
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partida", nullable = false)
    private GameEntity game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_questao", nullable = false)
    private QuestionEntity questao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alternativa")
    private AlternativeEntity alternativa;

    @Column(nullable = false)
    private boolean correta;

    @Column(name = "tempo_resposta", nullable = false)
    private int tempoResposta;
}
