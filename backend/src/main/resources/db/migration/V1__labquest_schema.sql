CREATE DATABASE IF NOT EXISTS etec_game
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE etec_game;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    salt VARCHAR(100) NOT NULL,
    tipo ENUM('ALUNO', 'PROFESSOR') NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email),

    INDEX idx_usuario_tipo (tipo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS material_laboratorio (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NULL,
    categoria VARCHAR(80) NOT NULL,
    funcao TEXT NULL,
    imagem_url VARCHAR(300) NULL,
    imagem_blob LONGBLOB NULL,

    CONSTRAINT pk_material PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sistema_experimental (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NULL,
    imagem_url VARCHAR(300) NULL,
    imagem_blob LONGBLOB NULL,

    CONSTRAINT pk_sistema PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sistema_material (
    id_sistema INT NOT NULL,
    id_material INT NOT NULL,

    CONSTRAINT pk_sistema_material PRIMARY KEY (id_sistema, id_material),
    CONSTRAINT fk_sm_sistema
        FOREIGN KEY (id_sistema)
        REFERENCES sistema_experimental (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_sm_material
        FOREIGN KEY (id_material)
        REFERENCES material_laboratorio (id)
        ON DELETE CASCADE,

    INDEX idx_sistema_material_material (id_material)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS questao (
    id INT NOT NULL AUTO_INCREMENT,
    enunciado TEXT NOT NULL,
    tipo ENUM('MULTIPLA_ESCOLHA', 'ASSOCIACAO') NOT NULL,
    nivel_dificuldade ENUM('FACIL', 'MEDIO', 'DIFICIL') NOT NULL,
    categoria VARCHAR(80) NULL,
    imagem_url VARCHAR(300) NULL,
    id_professor INT NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    criada_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_questao PRIMARY KEY (id),
    CONSTRAINT fk_questao_prof
        FOREIGN KEY (id_professor)
        REFERENCES usuario (id)
        ON DELETE RESTRICT,

    INDEX idx_questao_professor (id_professor),
    INDEX idx_questao_nivel (nivel_dificuldade),
    INDEX idx_questao_tipo (tipo),
    INDEX idx_questao_ativa (ativa)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS alternativa (
    id INT NOT NULL AUTO_INCREMENT,
    id_questao INT NOT NULL,
    texto TEXT NOT NULL,
    e_correta BOOLEAN NOT NULL DEFAULT FALSE,
    imagem_url VARCHAR(300) NULL,
    imagem_blob LONGBLOB NULL,

    CONSTRAINT pk_alternativa PRIMARY KEY (id),
    CONSTRAINT fk_alt_questao
        FOREIGN KEY (id_questao)
        REFERENCES questao (id)
        ON DELETE CASCADE,

    INDEX idx_alternativa_questao (id_questao)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS partida (
    id INT NOT NULL AUTO_INCREMENT,
    id_aluno INT NOT NULL,
    data_hora_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_hora_fim DATETIME NULL,
    pontuacao INT NOT NULL DEFAULT 0,
    nivel_atual ENUM('FACIL', 'MEDIO', 'DIFICIL') NOT NULL DEFAULT 'FACIL',
    finalizada BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_partida PRIMARY KEY (id),
    CONSTRAINT fk_partida_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES usuario (id)
        ON DELETE CASCADE,

    INDEX idx_partida_aluno (id_aluno)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS resposta (
    id INT NOT NULL AUTO_INCREMENT,
    id_partida INT NOT NULL,
    id_questao INT NOT NULL,
    id_alternativa INT NULL,
    correta BOOLEAN NOT NULL,
    tempo_resposta INT NOT NULL DEFAULT 0,

    CONSTRAINT pk_resposta PRIMARY KEY (id),
    CONSTRAINT fk_resp_partida
        FOREIGN KEY (id_partida)
        REFERENCES partida (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_resp_questao
        FOREIGN KEY (id_questao)
        REFERENCES questao (id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_resp_alternativa
        FOREIGN KEY (id_alternativa)
        REFERENCES alternativa (id)
        ON DELETE RESTRICT,

    INDEX idx_resposta_partida (id_partida),
    INDEX idx_resposta_questao (id_questao),
    INDEX idx_resposta_alternativa (id_alternativa)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS desempenho (
    id INT NOT NULL AUTO_INCREMENT,
    id_aluno INT NOT NULL,
    total_partidas INT NOT NULL DEFAULT 0,
    total_acertos INT NOT NULL DEFAULT 0,
    total_erros INT NOT NULL DEFAULT 0,
    percentual_acerto DOUBLE NOT NULL DEFAULT 0.0,
    nivel_medio ENUM('FACIL', 'MEDIO', 'DIFICIL') NOT NULL DEFAULT 'FACIL',
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_desempenho PRIMARY KEY (id),
    CONSTRAINT uk_desempenho_aluno UNIQUE (id_aluno),
    CONSTRAINT fk_desempenho_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES usuario (id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS relatorio (
    id INT NOT NULL AUTO_INCREMENT,
    id_aluno INT NOT NULL,
    id_professor INT NOT NULL,
    data_geracao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    periodo_inicio DATE NULL,
    periodo_fim DATE NULL,

    CONSTRAINT pk_relatorio PRIMARY KEY (id),
    CONSTRAINT fk_rel_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES usuario (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_rel_professor
        FOREIGN KEY (id_professor)
        REFERENCES usuario (id)
        ON DELETE RESTRICT,

    INDEX idx_relatorio_aluno (id_aluno),
    INDEX idx_relatorio_professor (id_professor)
) ENGINE=InnoDB;

START TRANSACTION;

INSERT INTO usuario (nome, email, senha, salt, tipo)
SELECT
    'Professor Teste',
    'professor@cps.sp.gov.br',
    'dxYZCj0kw7jeGlwRj65LBNvLHuMlYnsXsPc2vXTqyHU=',
    'saltProfessorTeste001',
    'PROFESSOR'
WHERE NOT EXISTS (
    SELECT 1
    FROM usuario
    WHERE email = 'professor@cps.sp.gov.br'
);

INSERT INTO usuario (nome, email, senha, salt, tipo)
SELECT
    'Aluno Teste',
    'aluno@aluno.cps.sp.gov.br',
    'kI3cKtVpYwnPJPr6ECGWbICwQNcAg9JbgnnRmNdmXgs=',
    'saltAlunoTeste002',
    'ALUNO'
WHERE NOT EXISTS (
    SELECT 1
    FROM usuario
    WHERE email = 'aluno@aluno.cps.sp.gov.br'
);

SET @id_professor := (
    SELECT id
    FROM usuario
    WHERE email = 'professor@cps.sp.gov.br'
    LIMIT 1
);

INSERT INTO desempenho (
    id_aluno,
    total_partidas,
    total_acertos,
    total_erros
)
SELECT
    u.id,
    0,
    0,
    0
FROM usuario AS u
WHERE u.tipo = 'ALUNO'
  AND NOT EXISTS (
      SELECT 1
      FROM desempenho AS d
      WHERE d.id_aluno = u.id
  );

SET @tipo := 'MULTIPLA_ESCOLHA';
SET @categoria := 'Materiais de laboratório';

-- Questão 1
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Qual a função do Béquer?',
    @tipo,
    'FACIL',
    @categoria,
    'imagens/Béquer.jpg',
    @id_professor,
    TRUE,
    NOW()
);

SET @q1 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q1, 'Misturar e aquecer líquidos', TRUE, NULL),
    (@q1, 'Transferir líquidos', FALSE, NULL),
    (@q1, 'Medir volume exato de líquido', FALSE, NULL),
    (@q1, 'Liberar volume controlado', FALSE, NULL);

-- Questão 2
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Qual a função do funil?',
    @tipo,
    'FACIL',
    @categoria,
    'imagens/FunilDeHasteLonga.jpg',
    @id_professor,
    TRUE,
    NOW()
);

SET @q2 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q2, 'Misturar líquidos', FALSE, NULL),
    (@q2, 'Aquecimento de soluções', FALSE, NULL),
    (@q2, 'Medir soluções', FALSE, NULL),
    (@q2, 'Transferir líquidos e auxiliar na filtração', TRUE, NULL);

-- Questão 3
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Qual material é usado em um sistema de titulação?',
    @tipo,
    'MEDIO',
    @categoria,
    'imagens/Bureta.jpg',
    @id_professor,
    TRUE,
    NOW()
);

SET @q3 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q3, 'Proveta', FALSE, NULL),
    (@q3, 'Bureta', TRUE, NULL),
    (@q3, 'Béquer', FALSE, NULL),
    (@q3, 'Bastão de vidro', FALSE, NULL);

-- Questão 4
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Qual material é usado em um sistema de mistura?',
    @tipo,
    'FACIL',
    @categoria,
    'imagens/Béquer.jpg',
    @id_professor,
    TRUE,
    NOW()
);

SET @q4 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q4, 'Pipeta', FALSE, NULL),
    (@q4, 'Bureta', FALSE, NULL),
    (@q4, 'Béquer', TRUE, NULL),
    (@q4, 'Funil', FALSE, NULL);

-- Questão 5
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Qual a função do bastão de vidro?',
    @tipo,
    'FACIL',
    @categoria,
    'imagens/BastãoDeVidro.jpg',
    @id_professor,
    TRUE,
    NOW()
);

SET @q5 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q5, 'Aquecimento', FALSE, NULL),
    (@q5, 'Titulação', FALSE, NULL),
    (@q5, 'Filtração', FALSE, NULL),
    (@q5, 'Misturar soluções', TRUE, NULL);

-- Questão 6
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Qual material é usado em um sistema de filtração?',
    @tipo,
    'FACIL',
    @categoria,
    'imagens/FunilDeHasteLonga.jpg',
    @id_professor,
    TRUE,
    NOW()
);

SET @q6 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q6, 'Funil', TRUE, NULL),
    (@q6, 'Pipeta', FALSE, NULL),
    (@q6, 'Bastão de vidro', FALSE, NULL),
    (@q6, 'Proveta', FALSE, NULL);

-- Questão 7
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Conecte o material ao sistema experimental correspondente',
    'ASSOCIACAO',
    'DIFICIL',
    @categoria,
    NULL,
    @id_professor,
    TRUE,
    NOW()
);

SET @q7 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q7, 'Lavagem do material', TRUE, 'imagens/Pisseta.jpg'),
    (@q7, 'Fonte de aquecimento', TRUE, 'imagens/BicoDeBunsen.jpg'),
    (@q7, 'Transferência de líquido', TRUE, 'imagens/PipetaPasteur.jpg'),
    (@q7, 'Recipiente de reação', TRUE, 'imagens/Erlenmeyer.jpg');

-- Questão 8
INSERT INTO questao (
    enunciado,
    tipo,
    nivel_dificuldade,
    categoria,
    imagem_url,
    id_professor,
    ativa,
    criada_em
) VALUES (
    'Conecte o material ao sistema experimental correspondente',
    'ASSOCIACAO',
    'DIFICIL',
    @categoria,
    NULL,
    @id_professor,
    TRUE,
    NOW()
);

SET @q8 := LAST_INSERT_ID();

INSERT INTO alternativa (id_questao, texto, e_correta, imagem_url) VALUES
    (@q8, 'Medição controlada', TRUE, 'imagens/Bureta.jpg'),
    (@q8, 'Preparo de solução', TRUE, 'imagens/BalãoVolumétrico.jpg'),
    (@q8, 'Distribuição de calor', TRUE, 'imagens/TelaDeAmianto.jpg'),
    (@q8, 'Medição de volume', TRUE, 'imagens/Proveta.jpg');

COMMIT;