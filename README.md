# Portal Cliente Solicitações

Portal onde o cliente final loga, completa o cadastro (dados pessoais, endereço, renda, anexos) por
etapas retomáveis, e cria/acompanha suas solicitações — reaproveitando os dados já cadastrados.

## Stack

| Camada | Tecnologia |
|---|---|
| Frontend | React 19 + TypeScript + Vite, React Router, Axios |
| Backend | Kotlin 2.3 + Spring Boot 4.1 (arquitetura hexagonal), Gradle |
| Dados | DynamoDB (single-table) |
| Auth | Amazon Cognito (User Pool), JWT validado no API Gateway e revalidado no backend |
| Anexos | Upload via backend → S3 → evento SQS |
| Infra | Terraform (rede, ECS Fargate, API Gateway HTTP API, Cognito, DynamoDB, S3, SQS) |
| Dev local | docker-compose + [ministack](https://github.com/ministackorg/ministack) (emulador AWS) |

Arquitetura completa e decisões de design: [`docs/architecture.md`](docs/architecture.md).

## Rodando local

### 1. Subir o ministack (emulador AWS)

```bash
docker compose up -d ministack
```

### 2. Aplicar a infra local (opcional — só se for exercitar o backend/frontend de ponta a ponta)

Ver [`infra/README.md`](infra/README.md) para o provider Terraform apontado pro ministack e as
env vars que o backend precisa.

### 3. Backend

```bash
cd backend
./gradlew bootRun
```

Requer `COGNITO_ISSUER_URI`, `AWS_ENDPOINT_OVERRIDE`, `AWS_DYNAMODB_TABLE_NAME`,
`AWS_S3_ATTACHMENTS_BUCKET`, `AWS_SQS_ATTACHMENTS_QUEUE_URL` setadas (ver `infra/README.md`).

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

Requer `.env` com `VITE_API_BASE_URL`, `VITE_COGNITO_USER_POOL_ID`, `VITE_COGNITO_CLIENT_ID`
(ver `frontend/.env.example`).

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
