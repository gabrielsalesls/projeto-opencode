# Sobre o projeto

Este repositório contém um projeto desenvolvido integralmente via vibecoding com o OpenCode, utilizando principalmente a LLM DeepSeek V4. O objetivo é validar a capacidade do agente em gerar código na versão gratuita da plataforma.

# Account API

API de contas bancárias com suporte a transferências entre contas.

## Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven

## Subir dependências (PostgreSQL + RabbitMQ)

```bash
docker compose up -d
```

> A porta `5433` é mapeada para o PostgreSQL do container. Caso tenha um PostgreSQL local na porta `5432`, não há conflito.

## Rodar a aplicação

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
