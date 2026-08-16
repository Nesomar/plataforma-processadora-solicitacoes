# Portal Cliente Solicitações

Portal onde o cliente final loga, completa o cadastro (dados pessoais, endereço, renda, anexos) por
etapas retomáveis, e cria/acompanha suas solicitações — reaproveitando os dados já cadastrados.

## Stack

| Camada | Tecnologia |
|---|---|
| Frontend | React 19 + TypeScript + Vite, React Router, Axios |
| Backend | Kotlin 2.3 + Spring Boot 4.1 (arquitetura hexagonal), Gradle |
| Dados | DynamoDB (single-table) |
| Auth | JWT próprio (backend emite e valida, HS256 — sem Cognito) |
| Anexos | Upload via backend → S3 → evento SQS |
| Infra | Terraform (rede, ECS Fargate, API Gateway HTTP API, DynamoDB, S3, SQS) |
| Dev local | docker-compose + [ministack](https://github.com/ministackorg/ministack) (emulador AWS) |

Arquitetura completa e decisões de design: [`docs/architecture.md`](docs/architecture.md).

## Rodando local

Um comando sobe tudo: ministack, aplica a infra Terraform local (`terraform-init`, roda uma vez
e sai), backend e frontend — cada um em container, backend/frontend só sobem depois que o
`terraform-init` termina com sucesso.

```bash
docker compose up --build
```

Backend em `http://localhost:8080`, frontend em `http://localhost:5173`. Detalhe de como o
`terraform-init` gera o provider/env locais: [`infra/README.md`](infra/README.md). Os módulos
`network`/`api_gateway`/`ecs` ficam fora do apply local: dependem de NLB + VPC Link, sem suporte
confirmado no ministack (gap documentado em `infra/README.md`).

Pra iterar só no backend ou frontend fora de container (mais rápido pra loop de dev): ver
["Rodando backend/frontend fora de container"](infra/README.md#rodando-backendfrontend-fora-de-container-loop-de-dev-mais-rápido)
em `infra/README.md` — os endpoints gerados usam o nome do serviço docker (`ministack`), precisa trocar
por `localhost` ao exportar manualmente.

## Testes

```bash
cd backend && ./gradlew test
cd frontend && npm test && npm run build
```

## Estrutura

```
backend/    Kotlin/Spring Boot, hexagonal (domain / port / application / adapter)
frontend/   React SPA
infra/      Terraform + docker-compose (ministack)
docs/       Arquitetura e decisões de design
openspec/   Specs das capabilities (client-auth, client-profile, attachments, service-requests)
```

## Escopo

Deploy real em conta AWS está fora de escopo deste projeto — a infra Terraform é AWS-alvo e fica
pronta para aplicação, mas a validação de ponta a ponta é feita localmente via ministack.
