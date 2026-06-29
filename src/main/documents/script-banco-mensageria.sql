-- ============================================================
-- SISTEMA DE MENSAGERIA INSTANTÂNEA
-- Projeto físico do banco de dados
-- MySQL / MariaDB
-- ============================================================

-- ATENÇÃO:
-- O comando abaixo apaga completamente o banco anterior.
-- Use apenas para recriar o ambiente de desenvolvimento.

DROP DATABASE IF EXISTS mensageria;

CREATE DATABASE mensageria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mensageria;

-- ============================================================
-- TABELA USUARIO
-- ============================================================

CREATE TABLE usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(150),
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(10) NOT NULL DEFAULT 'ATIVO',

    CONSTRAINT pk_usuario
        PRIMARY KEY (id_usuario),

    CONSTRAINT uk_usuario_telefone
        UNIQUE (telefone),

    CONSTRAINT uk_usuario_email
        UNIQUE (email),

    CONSTRAINT ck_usuario_status
        CHECK (status IN ('ATIVO', 'INATIVO'))
) ENGINE = InnoDB;

-- ============================================================
-- TABELA CONVERSA
-- ============================================================

CREATE TABLE conversa (
    id_conversa INT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(100),
    tipo VARCHAR(10) NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_conversa
        PRIMARY KEY (id_conversa),

    CONSTRAINT ck_conversa_tipo
        CHECK (tipo IN ('PRIVADA', 'GRUPO')),

    CONSTRAINT ck_conversa_titulo
        CHECK (
            tipo = 'PRIVADA'
            OR titulo IS NOT NULL
        )
) ENGINE = InnoDB;

-- ============================================================
-- TABELA PARTICIPANTE
-- ============================================================

CREATE TABLE participante (
    id_usuario INT NOT NULL,
    id_conversa INT NOT NULL,
    data_entrada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    funcao VARCHAR(15) NOT NULL DEFAULT 'MEMBRO',

    CONSTRAINT pk_participante
        PRIMARY KEY (id_usuario, id_conversa),

    CONSTRAINT fk_participante_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_participante_conversa
        FOREIGN KEY (id_conversa)
        REFERENCES conversa (id_conversa)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_participante_funcao
        CHECK (
            funcao IN ('MEMBRO', 'ADMINISTRADOR')
        )
) ENGINE = InnoDB;

-- ============================================================
-- TABELA MENSAGEM
-- ============================================================

CREATE TABLE mensagem (
    id_mensagem INT NOT NULL AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_conversa INT NOT NULL,
    conteudo TEXT NOT NULL,
    data_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_edicao DATETIME,
    status VARCHAR(10) NOT NULL DEFAULT 'ENVIADA',

    CONSTRAINT pk_mensagem
        PRIMARY KEY (id_mensagem),

    CONSTRAINT fk_mensagem_participante
        FOREIGN KEY (id_usuario, id_conversa)
        REFERENCES participante (id_usuario, id_conversa)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT ck_mensagem_status
        CHECK (
            status IN ('ENVIADA', 'EDITADA', 'REMOVIDA')
        ),

    CONSTRAINT ck_mensagem_conteudo
        CHECK (CHAR_LENGTH(TRIM(conteudo)) > 0)
) ENGINE = InnoDB;

-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX idx_participante_conversa
    ON participante (id_conversa);

CREATE INDEX idx_mensagem_conversa_data
    ON mensagem (id_conversa, data_envio);

CREATE INDEX idx_mensagem_usuario
    ON mensagem (id_usuario);

CREATE INDEX idx_usuario_nome
    ON usuario (nome);

-- ============================================================
-- DADOS DE EXEMPLO
-- ============================================================

INSERT INTO usuario
    (id_usuario, nome, telefone, email, status)
VALUES
    (1, 'Ana Souza', '47999990001',
        'ana.souza@email.com', 'ATIVO'),
    (2, 'Bruno Oliveira', '47999990002',
        'bruno.oliveira@email.com', 'ATIVO'),
    (3, 'Carla Santos', '47999990003',
        'carla.santos@email.com', 'ATIVO'),
    (4, 'Daniel Lima', '47999990004',
        'daniel.lima@email.com', 'ATIVO'),
    (5, 'Eduarda Martins', '47999990005',
        NULL, 'INATIVO');

INSERT INTO conversa
    (id_conversa, titulo, tipo)
VALUES
    (1, NULL, 'PRIVADA'),
    (2, 'Grupo do Trabalho de Banco de Dados', 'GRUPO'),
    (3, 'Amigos da Faculdade', 'GRUPO');

INSERT INTO participante
    (id_usuario, id_conversa, funcao)
VALUES
    (1, 1, 'MEMBRO'),
    (2, 1, 'MEMBRO'),
    (1, 2, 'ADMINISTRADOR'),
    (2, 2, 'MEMBRO'),
    (3, 2, 'MEMBRO'),
    (4, 2, 'MEMBRO'),
    (2, 3, 'ADMINISTRADOR'),
    (3, 3, 'MEMBRO'),
    (4, 3, 'MEMBRO');

INSERT INTO mensagem
    (
        id_mensagem,
        id_usuario,
        id_conversa,
        conteudo,
        data_envio,
        data_edicao,
        status
    )
VALUES
    (
        1, 1, 1,
        'Olá, Bruno! Tudo bem?',
        '2026-06-29 09:00:00',
        NULL,
        'ENVIADA'
    ),
    (
        2, 2, 1,
        'Tudo bem! E com você?',
        '2026-06-29 09:01:00',
        NULL,
        'ENVIADA'
    ),
    (
        3, 1, 2,
        'Criei o grupo para organizarmos o trabalho.',
        '2026-06-29 10:00:00',
        NULL,
        'ENVIADA'
    ),
    (
        4, 3, 2,
        'Eu posso ajudar com o diagrama.',
        '2026-06-29 10:05:00',
        NULL,
        'ENVIADA'
    ),
    (
        5, 4, 2,
        'Vou revisar o código SQL.',
        '2026-06-29 10:10:00',
        NULL,
        'ENVIADA'
    ),
    (
        6, 2, 2,
        'Posso implementar o CRUD em Java.',
        '2026-06-29 10:15:00',
        '2026-06-29 10:16:00',
        'EDITADA'
    ),
    (
        7, 2, 3,
        'Alguém quer estudar hoje?',
        '2026-06-29 11:00:00',
        NULL,
        'ENVIADA'
    ),
    (
        8, 3, 3,
        'Mensagem removida',
        '2026-06-29 11:05:00',
        NULL,
        'REMOVIDA'
    );

-- ============================================================
-- CONSULTAS DE TESTE
-- ============================================================

SELECT * FROM usuario;
SELECT * FROM conversa;
SELECT * FROM participante;
SELECT * FROM mensagem;

SELECT
    m.id_mensagem,
    u.nome AS autor,
    c.id_conversa,
    c.titulo,
    CASE
        WHEN m.status = 'REMOVIDA'
            THEN 'Mensagem removida'
        ELSE m.conteudo
    END AS conteudo,
    m.data_envio,
    m.data_edicao,
    m.status
FROM mensagem m
INNER JOIN usuario u
    ON u.id_usuario = m.id_usuario
INNER JOIN conversa c
    ON c.id_conversa = m.id_conversa
ORDER BY
    c.id_conversa,
    m.data_envio,
    m.id_mensagem;