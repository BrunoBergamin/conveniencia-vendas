# Sistema de Controle de Vendas de Conveniência

Backend em **microserviços Java** para o dia a dia de uma loja de conveniência:
cadastro de produtos, controle de estoque, registro de vendas, caixa por turno e
relatórios. Construído com duas stacks diferentes de propósito, para demonstrar
domínio das duas, e preparado para rodar em **Kubernetes**.

![CI](https://github.com/BrunoBergamin/conveniencia-vendas/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F)
![Quarkus](https://img.shields.io/badge/Quarkus-3.15-4695EB)
![Kubernetes](https://img.shields.io/badge/Kubernetes-ready-326CE5)
![License](https://img.shields.io/badge/license-MIT-black)

> Projeto de portfólio. O objetivo é mostrar arquitetura de microserviços,
> boas práticas de engenharia (Clean Architecture, DDD, testes, observabilidade,
> resiliência) e a operação em contêineres e Kubernetes.

## Arquitetura

Dois serviços independentes, cada um dono do seu banco (padrão *database per
service*), comunicando por REST. Um usa **Spring Boot**, o outro **Quarkus**, de
propósito.

```mermaid
flowchart LR
    Cliente([PDV / Frontend]) -->|HTTP + JWT| GW{{Ingress}}
    GW --> C[catalogo-service\nSpring Boot 3\nJava 21]
    GW --> V[vendas-service\nQuarkus 3\nJava 21]
    V -->|REST: valida e baixa estoque\ncom fault tolerance| C
    C --- CDB[(PostgreSQL\ncatalogo_db)]
    V --- VDB[(PostgreSQL\nvendas_db)]
    C -. login emite JWT .-> Cliente
    C -. valida JWT .- V
```

| Serviço | Stack | Contexto (DDD) | Porta | Responsabilidade |
|---|---|---|---|---|
| **catalogo-service** | Spring Boot 3, Java 21 | Catálogo e Estoque, Identidade | 8081 | Produtos, categorias, estoque, login e emissão de JWT |
| **vendas-service** | Quarkus 3, Java 21 | Vendas e Caixa | 8082 | Registro de venda, itens, caixa por turno, relatórios |

- **Autenticação:** o `catalogo-service` autentica o operador e emite um **JWT
  (HS256)**. Os dois serviços validam o token com um segredo compartilhado
  (injetado por *Secret* no Kubernetes). Em um sistema maior isso seria um
  `identity-service` dedicado; aqui fica no catálogo para manter o escopo enxuto,
  e a decisão está documentada em [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).
- **Comunicação entre serviços:** ao registrar uma venda, o `vendas-service`
  chama o `catalogo-service` para validar o produto e **dar baixa no estoque**,
  com *timeout*, *retry* e *circuit breaker* (fault tolerance).
- **Banco por serviço:** cada serviço tem seu PostgreSQL e suas migrações Flyway.
  Nada de banco compartilhado.

Detalhes das decisões, contexto delimitado e contrato entre serviços em
**[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)**.

## Funcionalidades

- **Produtos e categorias:** CRUD, código de barras, preço, categoria.
- **Estoque:** quantidade por produto, entrada de estoque, baixa transacional na
  venda, bloqueio de venda sem saldo.
- **Vendas:** registro com vários itens, cálculo de total, forma de pagamento,
  vínculo ao caixa aberto.
- **Caixa por turno:** abertura com fundo de troco, fechamento com conferência,
  uma venda só é aceita com caixa aberto.
- **Relatórios:** vendas do dia, total por forma de pagamento, produtos mais
  vendidos, resumo do turno.
- **Autenticação:** login com usuário e senha, JWT com papéis (OPERADOR, GERENTE),
  rotas protegidas por papel.

## Como rodar

### Opção 1: Docker Compose (mais simples)

Sobe os dois bancos e os dois serviços de uma vez.

```bash
docker compose up --build
```

- catalogo-service: http://localhost:8081  (Swagger em `/swagger-ui.html`)
- vendas-service:  http://localhost:8082  (Swagger em `/q/swagger-ui`)

Um roteiro de uso ponta a ponta (login, criar produto, dar entrada de estoque,
abrir caixa, registrar venda, ver relatório) está em
[docs/DEMO.md](docs/DEMO.md).

### Opção 2: Kubernetes

```bash
kubectl apply -k deploy/k8s
kubectl -n conveniencia get pods
```

Manifestos com Deployments, Services, ConfigMap, Secret, Ingress, HPA e probes de
saúde em [deploy/k8s](deploy/k8s). Passo a passo em
[deploy/k8s/README.md](deploy/k8s/README.md).

### Opção 3: rodar um serviço isolado (dev)

```bash
# catalogo-service (Spring Boot)
cd catalogo-service && ./mvnw spring-boot:run

# vendas-service (Quarkus, com live reload)
cd vendas-service && ./mvnw quarkus:dev
```

## Práticas de engenharia

O que este repositório demonstra de propósito:

- **Clean Architecture / Hexagonal:** domínio isolado de framework e de
  infraestrutura. As regras de negócio não dependem de Spring, Quarkus nem do
  banco.
- **DDD tático:** contextos delimitados por serviço, entidades e objetos de valor
  ricos, invariantes protegidas no domínio (ex.: estoque nunca fica negativo).
- **Testes em camadas:** unitários de domínio (rápidos, sem infra), de
  integração com **Testcontainers** (Postgres real) e de API (**MockMvc** no
  Spring, **RestAssured** no Quarkus).
- **Observabilidade:** health checks (liveness e readiness) e métricas Prometheus
  em ambos, prontos para o Kubernetes.
- **Resiliência:** timeout, retry e circuit breaker na chamada entre serviços.
- **Migrações versionadas:** Flyway em cada serviço, banco reproduzível.
- **Contrato explícito:** OpenAPI/Swagger gerado em cada serviço.
- **12-Factor:** configuração por variável de ambiente, segredos fora do código,
  imagens de contêiner imutáveis.
- **CI:** GitHub Actions compila e testa os dois serviços a cada push.

## Estrutura do repositório

```
conveniencia-vendas/
├── catalogo-service/     # Spring Boot 3 (produtos, estoque, auth)
├── vendas-service/       # Quarkus 3 (vendas, caixa, relatórios)
├── deploy/
│   └── k8s/              # manifestos Kubernetes (kustomize)
├── docs/
│   ├── ARCHITECTURE.md   # decisões, contextos, contrato entre serviços
│   └── DEMO.md           # roteiro de uso ponta a ponta
├── .github/workflows/    # CI (build + testes)
└── docker-compose.yml    # sobe tudo local
```

## Stack

Java 21, Spring Boot 3, Quarkus 3, PostgreSQL 16, Flyway, Maven, JWT (SmallRye /
Spring Security), OpenAPI, JUnit 5, Testcontainers, RestAssured, Docker,
Kubernetes (kustomize), GitHub Actions.

## Autor

**Bruno Alves Bergamin** — BV DevOps
[github.com/BrunoBergamin](https://github.com/BrunoBergamin) ·
[bvdevops.com.br](https://bvdevops.com.br)

Licença [MIT](LICENSE).
