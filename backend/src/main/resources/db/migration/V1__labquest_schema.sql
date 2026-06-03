CREATE TABLE IF NOT EXISTS usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    salt VARCHAR(100) NOT NULL,
    tipo ENUM('ALUNO', 'PROFESSOR') NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS material_laboratorio (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NULL,
    categoria VARCHAR(80) NOT NULL,
    funcao TEXT NULL,
    imagem_url VARCHAR(300) NULL,
    imagem_blob LONGBLOB NULL,
    CONSTRAINT pk_material PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sistema_experimental (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NULL,
    imagem_url VARCHAR(300) NULL,
    imagem_blob LONGBLOB NULL,
    CONSTRAINT pk_sistema PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sistema_material (
    id_sistema INT NOT NULL,
    id_material INT NOT NULL,
    CONSTRAINT pk_sistema_material PRIMARY KEY (id_sistema, id_material),
    CONSTRAINT fk_sm_sistema FOREIGN KEY (id_sistema) REFERENCES sistema_experimental(id) ON DELETE CASCADE,
    CONSTRAINT fk_sm_material FOREIGN KEY (id_material) REFERENCES material_laboratorio(id) ON DELETE CASCADE
);

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
    CONSTRAINT fk_questao_prof FOREIGN KEY (id_professor) REFERENCES usuario(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS alternativa (
    id INT NOT NULL AUTO_INCREMENT,
    id_questao INT NOT NULL,
    texto TEXT NOT NULL,
    e_correta BOOLEAN NOT NULL DEFAULT FALSE,
    imagem_url VARCHAR(300) NULL,
    imagem_blob LONGBLOB NULL,
    CONSTRAINT pk_alternativa PRIMARY KEY (id),
    CONSTRAINT fk_alt_questao FOREIGN KEY (id_questao) REFERENCES questao(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS partida (
    id INT NOT NULL AUTO_INCREMENT,
    id_aluno INT NOT NULL,
    data_hora_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_hora_fim DATETIME NULL,
    pontuacao INT NOT NULL DEFAULT 0,
    nivel_atual ENUM('FACIL', 'MEDIO', 'DIFICIL') NOT NULL DEFAULT 'FACIL',
    finalizada BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_partida PRIMARY KEY (id),
    CONSTRAINT fk_partida_aluno FOREIGN KEY (id_aluno) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resposta (
    id INT NOT NULL AUTO_INCREMENT,
    id_partida INT NOT NULL,
    id_questao INT NOT NULL,
    id_alternativa INT NULL,
    correta BOOLEAN NOT NULL,
    tempo_resposta INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_resposta PRIMARY KEY (id),
    CONSTRAINT fk_resp_partida FOREIGN KEY (id_partida) REFERENCES partida(id) ON DELETE CASCADE,
    CONSTRAINT fk_resp_questao FOREIGN KEY (id_questao) REFERENCES questao(id) ON DELETE RESTRICT,
    CONSTRAINT fk_resp_alternativa FOREIGN KEY (id_alternativa) REFERENCES alternativa(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS desempenho (
    id INT NOT NULL AUTO_INCREMENT,
    id_aluno INT NOT NULL UNIQUE,
    total_partidas INT NOT NULL DEFAULT 0,
    total_acertos INT NOT NULL DEFAULT 0,
    total_erros INT NOT NULL DEFAULT 0,
    percentual_acerto DOUBLE NOT NULL DEFAULT 0.0,
    nivel_medio ENUM('FACIL', 'MEDIO', 'DIFICIL') NOT NULL DEFAULT 'FACIL',
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_desempenho PRIMARY KEY (id),
    CONSTRAINT fk_desempenho_aluno FOREIGN KEY (id_aluno) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS relatorio (
    id INT NOT NULL AUTO_INCREMENT,
    id_aluno INT NOT NULL,
    id_professor INT NOT NULL,
    data_geracao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    periodo_inicio DATE NULL,
    periodo_fim DATE NULL,
    CONSTRAINT pk_relatorio PRIMARY KEY (id),
    CONSTRAINT fk_rel_aluno FOREIGN KEY (id_aluno) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_rel_professor FOREIGN KEY (id_professor) REFERENCES usuario(id) ON DELETE RESTRICT
);

ALTER TABLE resposta ADD COLUMN IF NOT EXISTS tempo_resposta INT NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);
CREATE INDEX IF NOT EXISTS idx_usuario_tipo ON usuario(tipo);
CREATE INDEX IF NOT EXISTS idx_questao_nivel ON questao(nivel_dificuldade);
CREATE INDEX IF NOT EXISTS idx_questao_tipo ON questao(tipo);
CREATE INDEX IF NOT EXISTS idx_questao_ativa ON questao(ativa);
CREATE INDEX IF NOT EXISTS idx_partida_aluno ON partida(id_aluno);
CREATE INDEX IF NOT EXISTS idx_resposta_partida ON resposta(id_partida);
CREATE INDEX IF NOT EXISTS idx_resposta_questao ON resposta(id_questao);

INSERT INTO usuario (nome, email, senha, salt, tipo)
SELECT 'Professor Teste', 'professor@cps.sp.gov.br', 'dxYZCj0kw7jeGlwRj65LBNvLHuMlYnsXsPc2vXTqyHU=', 'saltProfessorTeste001', 'PROFESSOR'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'professor@cps.sp.gov.br');

INSERT INTO usuario (nome, email, senha, salt, tipo)
SELECT 'Aluno Teste', 'aluno@aluno.cps.sp.gov.br', 'kI3cKtVpYwnPJPr6ECGWbICwQNcAg9JbgnnRmNdmXgs=', 'saltAlunoTeste002', 'ALUNO'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'aluno@aluno.cps.sp.gov.br');

INSERT INTO desempenho (id_aluno, total_partidas, total_acertos, total_erros)
SELECT u.id, 0, 0, 0
FROM usuario u
WHERE u.tipo = 'ALUNO'
AND NOT EXISTS (SELECT 1 FROM desempenho d WHERE d.id_aluno = u.id);
