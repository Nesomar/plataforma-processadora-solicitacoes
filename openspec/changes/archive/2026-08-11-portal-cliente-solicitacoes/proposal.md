## Why

Não existe hoje um portal onde o cliente final acompanhe suas solicitações ou abra uma nova. O cadastro completo (dados pessoais, endereço, renda, documentos) precisa acontecer antes da primeira solicitação, mas isso não pode ser um formulário monolítico — precisa ser feito por etapas, com retomada caso o cliente saia no meio. É a base do sistema: sem login, cadastro e fluxo de solicitação, não há produto.

## What Changes

- Login do cliente via Cognito (API Gateway com Cognito Authorizer valida o JWT; ECS revalida a assinatura via Spring Security resource server).
- Wizard de onboarding sequencial (dados pessoais → endereço → renda → anexos), com persistência parcial: cada etapa grava um draft imediatamente (PATCH), permitindo retomar de onde parou.
- Gate "perfil completo": a cada login/request, consulta DynamoDB para decidir se o cliente cai no wizard ou direto no dashboard (sem depender de claim no token, que ficaria desatualizado).
- Upload de anexos passando pelo ECS (não presigned direto do cliente para o S3): ECS grava no S3 e publica em fila SQS. Sem validação de conteúdo no MVP (PDFs simples) — a fila já entra desde já como upgrade-path para quando a validação/scan for adicionada, evitando redesenho futuro.
- Dashboard do cliente: listar suas próprias solicitações (sem consulta por operador/interna nesta fase) e criar nova solicitação.
- Nova solicitação reaproveita os dados do perfil já cadastrado (não repete endereço/renda/dados pessoais).
- Backend em Kotlin + Spring Boot (versões mais recentes), arquitetura hexagonal, DynamoDB em single-table design.
- Frontend em React, build estático servido via S3 + CloudFront.
- Ambiente local via docker-compose com ministack (emulador AWS) cobrindo ECS, API Gateway, DynamoDB, S3, SQS e Cognito.
- Infraestrutura AWS provisionada via Terraform (rede, NLB/VPC Link na frente do ECS, ECS service, API Gateway, Cognito User Pool, tabela DynamoDB, buckets S3, fila SQS, IAM).

## Capabilities

### New Capabilities
- `client-auth`: login do cliente, emissão/validação de JWT via Cognito, revalidação no backend.
- `client-profile`: wizard de onboarding sequencial com persistência parcial por etapa (dados pessoais, endereço, renda) e o gate de perfil completo.
- `attachments`: upload de anexos via ECS, gravação em S3, publicação em fila SQS.
- `service-requests`: criação de nova solicitação (reaproveitando o perfil) e listagem/acompanhamento das solicitações do próprio cliente.

### Modified Capabilities
(nenhuma — projeto novo, sem specs existentes)

## Impact

- Novo backend Kotlin/Spring Boot (hexagonal) rodando em ECS Fargate.
- Novo frontend React (SPA) em S3+CloudFront.
- Nova infraestrutura AWS via Terraform: VPC/rede, NLB+VPC Link, ECS, API Gateway, Cognito User Pool, DynamoDB (tabela única), buckets S3 (frontend + anexos), fila SQS.
- Novo docker-compose local com ministack para desenvolvimento/teste sem depender de conta AWS real.
- Convenção de branch por etapa: `feature/{nome-da-funcionalidade}`, com code review ao final de cada etapa.
