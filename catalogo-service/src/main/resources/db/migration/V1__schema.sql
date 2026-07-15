create table produto (
    id            uuid          primary key,
    codigo_barras varchar(64)   not null unique,
    descricao     varchar(160)  not null,
    preco         numeric(12, 2) not null check (preco >= 0),
    categoria     varchar(20)   not null,
    ativo         boolean       not null default true
);

create table estoque (
    produto_id uuid    primary key references produto (id),
    quantidade integer not null default 0 check (quantidade >= 0),
    versao     bigint  not null default 0
);

create table usuario (
    id         uuid         primary key,
    login      varchar(60)  not null unique,
    senha_hash varchar(100) not null,
    papel      varchar(20)  not null
);

create index idx_produto_categoria on produto (categoria);
