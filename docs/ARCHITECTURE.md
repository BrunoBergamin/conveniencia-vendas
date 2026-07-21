# Arquitetura

Documento de decisões (ADR resumido), contextos delimitados e o contrato entre os
serviços.

## Visão geral

O sistema é dividido em dois contextos delimitados (DDD), cada um virando um
microserviço com banco próprio:

- **Catálogo e Estoque** (`catalogo-service`, Spring Boot): o que a loja vende e
  quanto tem. Também guarda os usuários e emite o token de login (ver decisão 4).
- **Vendas e Caixa** (`vendas-service`, Quarkus): o que foi vendido, para qual
  caixa, e os relatórios.

A venda é a operação que cruza os dois contextos: o `vendas-service` orquestra,
mas quem manda no estoque é o `catalogo-service`.

## Decisões

### 1. Duas stacks de propósito (Spring Boot + Quarkus)

Um projeto de portfólio precisa mostrar as duas. Além disso, elas têm perfis
diferentes: Spring Boot pelo ecossistema e produtividade; Quarkus pelo *startup*
rápido e baixo consumo de memória, bom para escalar horizontalmente no Kubernetes.
Colocar o serviço de vendas (o que mais escala em pico) no Quarkus é coerente.

### 2. Banco por serviço (database per service)

Cada serviço é dono do seu schema e ninguém acessa o banco do outro direto. Isso
mantém o acoplamento no nível do contrato REST, não do banco. O preço é a
consistência eventual e a necessidade de orquestração na venda (decisão 3).

### 3. Baixa de estoque síncrona com fault tolerance

Na venda, o `vendas-service` chama o `catalogo-service` para validar o produto e
baixar o estoque **antes** de confirmar a venda. É uma escolha de consistência:
não registrar venda de item sem saldo. A chamada é protegida por timeout, retry e
circuit breaker; se o catálogo estiver fora, a venda falha de forma limpa (não
grava pela metade).

> A chamada síncrona continua, mas hoje ela é **idempotente e compensável**
> (ver decisão 6), o que fecha as brechas de retry duplicado e de falha no meio
> do caminho. Evolução natural: trocar por mensageria (Kafka) com reserva e
> confirmação, para desacoplar de vez; a fronteira já está isolada num *port*
> de saída, então a troca é local.

### 4. Autenticação no catálogo, validação nos dois

O `catalogo-service` tem os usuários e expõe `POST /auth/login`, que devolve um
**JWT HS256** com o papel do operador. Os dois serviços validam o token com o
mesmo segredo (injetado por *Secret* no Kubernetes, nunca no código).

Num sistema real isso seria um `identity-service` dedicado. Aqui fica no catálogo
para não inflar o escopo do portfólio. A fronteira está clara: só o módulo de
identidade emite; todo o resto só valida.

### 5. Clean Architecture em cada serviço

Camadas: `domain` (entidades, objetos de valor, regras, sem framework) → `application`
(casos de uso, *ports*) → `infrastructure` (JPA/Panache, REST client, segurança) →
`api` (controllers/resources, DTOs). A dependência aponta sempre para dentro: o
domínio não conhece Spring, Quarkus nem Postgres.

### 6. Idempotência ponta a ponta e compensação (saga)

A venda é uma mini-saga de dois passos (baixa de estoque remota + persistência
local), e dois problemas clássicos aparecem: **retry duplicado** (timeout na
primeira chamada + retry = baixa dupla) e **falha no meio do caminho** (estoque
baixado, venda não gravada). A solução tem três peças:

1. **Chave de idempotência ponta a ponta**: o frontend gera um UUID por venda e
   só troca a chave depois de venda confirmada. O `vendas-service` deduplica o
   registro por essa chave (constraint unique no banco) e a repassa ao catálogo
   no header `Idempotency-Key`.
2. **Baixa idempotente no catálogo**: cada baixa é registrada na tabela
   `operacao_estoque` com a chave única. Se a mesma chave chegar de novo (retry
   após timeout), o catálogo devolve a resposta gravada da primeira vez, sem
   baixar o estoque outra vez. É isso que torna o retry automático seguro.
3. **Compensação**: se a persistência da venda falhar depois do estoque baixado,
   o `vendas-service` pede o estorno da baixa (`POST /internal/estoque/estornar`
   com a mesma chave). O estorno repõe exatamente o que a baixa tirou, marca a
   operação como `ESTORNADA` e também é idempotente: estornar duas vezes não
   repõe duas vezes, e estornar chave desconhecida é *no-op*.

Corrida entre requisições com a mesma chave é resolvida no banco: a constraint
unique derruba a segunda, e o código devolve o registro que ganhou a corrida.
Pior caso da saga (baixa sem venda e estorno falhou): log `RECONCILIAR` em nível
de erro, para reconciliação manual ou um futuro job de reconciliação.

## Contrato entre serviços

O `vendas-service` consome estas rotas do `catalogo-service`:

### Baixar estoque (usado na venda)

```
POST /internal/estoque/baixar
Authorization: Bearer <jwt>
Idempotency-Key: <uuid da venda>
Content-Type: application/json

{
  "itens": [
    { "produtoId": "uuid", "quantidade": 2 }
  ]
}
```

Respostas:
- `200 OK` com os produtos e preços aplicados (fonte de verdade do preço é o
  catálogo):
  ```json
  { "itens": [ { "produtoId": "uuid", "descricao": "Coca 350ml", "precoUnitario": 5.50, "quantidade": 2 } ] }
  ```
  Chave repetida (retry) devolve `200 OK` com a resposta gravada da primeira
  operação, sem baixar o estoque de novo.
- `409 Conflict` se algum item não tem saldo (`{ "erro": "SEM_ESTOQUE", "produtoId": "uuid" }`).
- `409 Conflict` se a chave já foi estornada (`{ "erro": "OPERACAO_ESTORNADA" }`).
- `404 Not Found` se o produto não existe.

### Estornar baixa (compensação de venda que falhou)

```
POST /internal/estoque/estornar
Authorization: Bearer <jwt>
Idempotency-Key: <uuid da venda>
```

Resposta: `200 OK` com `{ "estornada": true }` quando a baixa foi reposta, ou
`{ "estornada": false }` como *no-op* (chave desconhecida ou já estornada antes).

### Consultar produto

```
GET /produtos/{id}
Authorization: Bearer <jwt>
```

O preço aplicado na venda vem sempre do catálogo, nunca do cliente, para o PDV não
conseguir forjar preço.

## Modelo de domínio (resumo)

**catalogo-service**
- `Produto` (id, codigoBarras, descricao, `Preco` (VO), `Categoria`, ativo)
- `Estoque` (produtoId, quantidade) com invariante: quantidade nunca negativa
- `OperacaoEstoque` (chave unique, itens baixados, status: EFETIVADA | ESTORNADA),
  o registro que dá idempotência à baixa e permite o estorno exato (decisão 6)
- `Usuario` (login, senha hash, `Papel`: OPERADOR | GERENTE)

**vendas-service**
- `Caixa` (id, operador, aberturaEm, `Dinheiro` fundoTroco, status: ABERTO | FECHADO)
- `Venda` (id, chaveIdempotencia unique, caixaId, `List<ItemVenda>`, `FormaPagamento`, total, criadaEm)
- `ItemVenda` (produtoId, descricao, `Dinheiro` precoUnitario, quantidade)
- Invariante: venda só é aceita com um caixa ABERTO do operador.

## Observabilidade

- **Health:** `/actuator/health/{liveness,readiness}` (Spring) e `/q/health/{live,ready}`
  (Quarkus), consumidos pelas probes do Kubernetes.
- **Métricas:** `/actuator/prometheus` (Spring) e `/q/metrics` (Quarkus), no formato
  Prometheus, com anotações de scrape nos Deployments.

## Portas e ambientes

| Variável | Serviço | Uso |
|---|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | ambos | conexão do Postgres do próprio serviço |
| `JWT_SECRET` | ambos | segredo HS256 (via Secret no K8s) |
| `CATALOGO_URL` | vendas | base do catalogo-service para o REST client |

Nada de segredo em código ou em arquivo versionado. Local usa `docker-compose.yml`;
em cluster, `ConfigMap` (config) e `Secret` (senhas e JWT).
