# Sobre o projeto

Este repositório contém um projeto desenvolvido integralmente via vibecoding com o OpenCode, utilizando principalmente a LLM DeepSeek V4. O objetivo é validar a capacidade do agente em gerar código na versão gratuita da plataforma.

# Account API

API de contas bancárias com suporte a transferências entre contas.

## Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven
- `opentelemetry-javaagent.jar` na raiz do projeto (para telemetria via Docker)

> Baixe o agente em: https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest
> e coloque o arquivo `opentelemetry-javaagent.jar` na raiz do projeto.

## Subir tudo via Docker (aplicações + observabilidade)

### 1. Criar a network compartilhada (uma vez)

```bash
docker network create observability-net
```

### 2. Subir o stack de observabilidade

```bash
docker compose -f docker-compose.observability.yml up -d
```

### 3. Subir as aplicações

```bash
docker compose up -d --build
```

As aplicações enviam traces, métricas e logs automaticamente ao Collector via OTLP gRPC (`http://otel-collector:4317`).

### Ordem recomendada

A network deve existir antes de subir qualquer compose. O stack de observabilidade deve subir antes das aplicações para que o Collector já esteja pronto para receber telemetria.

## Subir apenas dependências (PostgreSQL + RabbitMQ)

```bash
docker compose up -d postgresql rabbitmq
```

> A porta `5433` é mapeada para o PostgreSQL do container. Caso tenha um PostgreSQL local na porta `5432`, não há conflito.

## Rodar a aplicação localmente (alternativa ao Docker)

```bash
cd account-api
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Health check

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada: `{"status":"UP"}`

## Endpoints

### Criar conta

```bash
curl -X POST http://localhost:8080/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerName":"Alice"}'
```

### Consultar conta

```bash
curl http://localhost:8080/v1/accounts/{id}
```

### Transferir

```bash
curl -X POST http://localhost:8080/v1/transfers \
  -H "Content-Type: application/json" \
  -d '{"sourceAccountId":"{id-origem}","destinationAccountId":"{id-destino}","amount":50.00}'
```

> Substitua `{id-origem}` e `{id-destino}` pelos UUIDs retornados na criação das contas.

## Testar com Bruno

Importe a coleção em `bruno/` no [Bruno API Client](https://www.usebruno.com/).

## Executar testes

```bash
cd account-api
mvn test
```

## Stack de Observabilidade

Infraestrutura local de observabilidade isolada das aplicações, composta por:

- **OpenTelemetry Collector** — recebe telemetria (traces, métricas, logs) via OTLP e encaminha para os backends
- **Prometheus** — armazena métricas (scrape no exporter do Collector)
- **Loki** — armazena logs (ingestão OTLP)
- **Tempo** — armazena traces (ingestão OTLP)
- **Grafana** — visualização com datasources já provisionados (sem dashboards)

Fluxo:

```
Aplicações → OTel Collector (OTLP 4317/4318)
                    ├── traces  → Tempo (3200)
                    ├── metrics → Prometheus (9090)
                    └── logs    → Loki (3100)
                                                    → Grafana (3000)
```

### Subir o stack

```bash
docker compose -f docker-compose.observability.yml up -d
```

### Acessos

| Serviço      | URL                    | Usuário/senha |
|--------------|------------------------|---------------|
| Grafana      | http://localhost:3000  | admin / admin |
| Prometheus   | http://localhost:9090  | —             |
| Loki         | http://localhost:3100  | —             |
| Tempo        | http://localhost:3200  | —             |
| OTel Collector | OTLP gRPC `localhost:4317` / HTTP `localhost:4318` | — |

> Os datasources (Prometheus, Loki e Tempo) são provisionados automaticamente no Grafana. Nenhum dashboard é criado.

### Derrubar o stack

```bash
docker compose -f docker-compose.observability.yml down
```

Para remover também os volumes (dados de telemetria):

```bash
docker compose -f docker-compose.observability.yml down -v
```

> As aplicações enviam telemetria ao Collector via Docker: o `docker-compose.yml` monta o `opentelemetry-javaagent.jar` como volume e usa `JAVA_TOOL_OPTIONS` para anexá-lo à JVM. O endpoint é `http://otel-collector:4317` via network `observability-net`.
>
> Suba o stack de observabilidade antes de iniciar as aplicações.
