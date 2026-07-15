# Roteiro de uso (ponta a ponta)

Com tudo rodando via `docker compose up --build`. O catalogo responde em
`:8081` e o vendas em `:8082`. Usuarios padrao criados no boot:
`gerente/gerente123` (papel GERENTE) e `operador/operador123` (papel OPERADOR).

## 1. Login (pega o token JWT)

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"login":"gerente","senha":"gerente123"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')
echo "$TOKEN"
```

## 2. Cadastrar um produto (GERENTE)

```bash
curl -s -X POST http://localhost:8081/produtos \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"codigoBarras":"7890000000001","descricao":"Chocolate 90g","preco":6.90,"categoria":"ALIMENTO"}'
```

Anote o `id` retornado (chamaremos de `PRODUTO_ID`). Ja existem produtos de
exemplo do seed, veja `GET /produtos`.

## 3. Dar entrada de estoque (GERENTE)

```bash
curl -s -X POST http://localhost:8081/estoque/PRODUTO_ID/entrada \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"quantidade":50}'
```

## 4. Abrir o caixa (no vendas-service)

O mesmo token vale nos dois serviços (mesmo segredo JWT).

```bash
curl -s -X POST http://localhost:8082/caixas \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"fundoTroco":100.00}'
```

## 5. Registrar uma venda

O vendas chama o catalogo para baixar o estoque e pegar o preco.

```bash
curl -s -X POST http://localhost:8082/vendas \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"itens":[{"produtoId":"PRODUTO_ID","quantidade":2}],"formaPagamento":"PIX"}'
```

A resposta traz o total calculado com o preco do catalogo.

## 6. Ver o relatorio do dia

```bash
curl -s "http://localhost:8082/relatorios/dia" -H "Authorization: Bearer $TOKEN"
```

## Swagger

- Catalogo: http://localhost:8081/swagger-ui.html
- Vendas:   http://localhost:8082/q/swagger-ui

## O que observar

- Tente vender mais do que tem em estoque: o catalogo devolve `409 SEM_ESTOQUE` e a
  venda nao e registrada.
- Tente registrar venda sem abrir o caixa: `409 CAIXA_NAO_ABERTO`.
- Derrube o catalogo (`docker compose stop catalogo-service`) e tente vender: o
  circuit breaker devolve `503 CATALOGO_INDISPONIVEL` de forma limpa.
