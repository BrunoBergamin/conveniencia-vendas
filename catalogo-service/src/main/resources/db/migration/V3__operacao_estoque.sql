-- Registro de operacoes de baixa de estoque (idempotencia + compensacao).
-- A chave e gerada pelo vendas-service e viaja no header Idempotency-Key:
-- repetir a mesma chave NAO baixa o estoque de novo (devolve a resposta gravada),
-- e o estorno (compensacao da saga) repoe exatamente o que a baixa tirou.
create table operacao_estoque (
    id            uuid         primary key,
    chave         uuid         not null unique,
    status        varchar(20)  not null,
    resposta_json text         not null,
    criada_em     timestamp(6) with time zone not null,
    estornada_em  timestamp(6) with time zone
);
