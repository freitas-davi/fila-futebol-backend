CREATE TABLE sessao (
    id         BIGSERIAL PRIMARY KEY,
    data       DATE        NOT NULL,
    status     VARCHAR(20) NOT NULL,
    criado_em  TIMESTAMP   NOT NULL DEFAULT now()
);

-- garante no banco que só existe uma sessão ativa por vez
CREATE UNIQUE INDEX uq_sessao_ativa ON sessao (status) WHERE status = 'ATIVA';

CREATE TABLE jogador_sessao (
    id            BIGSERIAL PRIMARY KEY,
    sessao_id     BIGINT       NOT NULL REFERENCES sessao (id),
    nome          VARCHAR(60)  NOT NULL,
    categoria     VARCHAR(1)   CHECK (categoria IN ('A', 'B', 'C', 'D')),
    status        VARCHAR(10)  NOT NULL DEFAULT 'FILA',
    lado          VARCHAR(1)   CHECK (lado IN ('A', 'B')),
    posicao_fila  INT          NOT NULL,
    UNIQUE (sessao_id, posicao_fila)
);

CREATE TABLE partida (
    id             BIGSERIAL PRIMARY KEY,
    sessao_id      BIGINT       NOT NULL REFERENCES sessao (id),
    numero         INT          NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    resultado      VARCHAR(10),
    iniciada_em    TIMESTAMP    NOT NULL DEFAULT now(),
    finalizada_em  TIMESTAMP,
    UNIQUE (sessao_id, numero)
);

CREATE TABLE partida_jogador (
    partida_id  BIGINT       NOT NULL REFERENCES partida (id),
    jogador_id  BIGINT       NOT NULL REFERENCES jogador_sessao (id),
    lado        VARCHAR(1)   NOT NULL CHECK (lado IN ('A', 'B')),
    PRIMARY KEY (partida_id, jogador_id)
);