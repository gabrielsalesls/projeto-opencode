# AGENTS.md

## Objetivo do Projeto

Este projeto existe para estudar:

* Desenvolvimento assistido por IA
* Arquitetura distribuída
* Outbox Pattern
* RabbitMQ
* PostgreSQL
* Spring Boot

A prioridade é aprendizado e clareza de código, não velocidade de entrega.

---

## Estrutura do Repositório

* account-api
* outbox-worker
* notification-api

Cada módulo deve permanecer independente.

---

## Regras de Desenvolvimento

Ao implementar uma tarefa:

* Faça apenas a alteração solicitada.
* Não implemente funcionalidades futuras.
* Não faça refatorações não solicitadas.
* Não altere arquivos fora do escopo da tarefa.
* Prefira soluções simples.
* Explique sempre quais arquivos foram modificados.
* Para cada alteração e arquivo criado na API, gere testes unitários validando as mudanças.
* Commits só podem ser feitos se nenhum teste unitário estiver quebrado.
* Commits só podem ser feitos quando explicitamente solicitado pelo usuário.

---

## Estratégia de Commits

Objetivo:

* Commits pequenos.
* Fácil revisão humana.
* Fácil entendimento do histórico.

Evitar:

* Commits com múltiplas funcionalidades.
* Grandes refatorações.
* Mudanças arquiteturais não solicitadas.

Meta:

* Preferencialmente menos de 300 linhas alteradas por commit.

Padrão:

* Commits semânticos: `tipo: mensagem`
* Tipos: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`
* Exemplos:
  * `feat: add docker-compose with PostgreSQL and RabbitMQ`
  * `fix: correct account balance calculation`
  * `chore: update AGENTS.md with commit convention`

---

## Tecnologias

Backend:

* Java 21
* Spring Boot

Banco:

* PostgreSQL
* Flyway

Mensageria:

* RabbitMQ

Infra:

* Docker Compose

---

## Arquitetura

Fluxo principal:

Account API
↓
Outbox Table
↓
Outbox Worker
↓
RabbitMQ
↓
Notification API

---

## Quando houver dúvida

Priorizar:

1. Simplicidade
2. Legibilidade
3. Menor alteração possível
4. Entrega incremental
