-- Chave de idempotencia da venda: gerada pelo frontend do caixa e unica.
-- Repetir a mesma requisicao (retry, clique duplo) nao cria venda duplicada.
-- Backfill: vendas antigas usam o proprio id como chave.
alter table venda add column chave_idempotencia uuid;
update venda set chave_idempotencia = id where chave_idempotencia is null;
alter table venda alter column chave_idempotencia set not null;
alter table venda add constraint uk_venda_chave_idempotencia unique (chave_idempotencia);
