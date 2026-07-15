create table caixa (
    id            uuid          primary key,
    operador      varchar(60)   not null,
    abertura_em   timestamp(6) with time zone not null,
    fundo_troco   numeric(12, 2) not null,
    status        varchar(20)   not null,
    fechamento_em timestamp(6) with time zone
);

create table venda (
    id              uuid          primary key,
    caixa_id        uuid          not null,
    forma_pagamento varchar(20)   not null,
    total           numeric(12, 2) not null,
    criada_em       timestamp(6) with time zone not null
);

create table item_venda (
    id             uuid          primary key,
    venda_id       uuid          not null references venda (id),
    produto_id     uuid          not null,
    descricao      varchar(160)  not null,
    preco_unitario numeric(12, 2) not null,
    quantidade     integer       not null
);

create index idx_venda_criada_em on venda (criada_em);
create index idx_venda_caixa on venda (caixa_id);
create index idx_item_venda_venda on item_venda (venda_id);
create index idx_caixa_operador_status on caixa (operador, status);
