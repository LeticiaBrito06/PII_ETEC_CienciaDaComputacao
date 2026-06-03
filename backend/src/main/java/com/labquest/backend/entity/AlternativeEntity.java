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
@Table(name = "alternativa")
public class AlternativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_questao", nullable = false)
    private QuestionEntity questao;

    @Column(nullable = false, columnDefinition = "texto")
    private String texto;

    @Column(name = "e_correta", nullable = false)
    private boolean correta;

    @Column(name = "imagem_url", length = 300)
    private String imagemUrl;
}
