# backend-performance

## Purpose

Requisitos de desempenho/escalabilidade do runtime do backend — modelo de processamento de
requisições HTTP sob carga concorrente, independente da capability de negócio exposta.

## Requirements

### Requirement: Processamento de requisições sem bloqueio de thread em I/O externo
O backend SHALL processar requisições HTTP sem bloquear uma thread da plataforma enquanto aguarda respostas de chamadas de I/O externo (DynamoDB, S3, SQS).

#### Scenario: Requisição concorrente durante chamada ao DynamoDB
- **WHEN** múltiplas requisições chegam simultaneamente e cada uma depende de uma leitura/escrita no DynamoDB
- **THEN** o backend atende todas sem que o número de requisições em andamento fique limitado pelo tamanho do pool de threads da plataforma (Netty event loop, não thread-per-request)

#### Scenario: Contrato de API inalterado
- **WHEN** um cliente já integrado (frontend ou teste) chama qualquer endpoint existente (`/api/perfil/*`, `/api/anexos/*`, `/api/solicitacoes/*`, `/api/me`)
- **THEN** a resposta (status HTTP, corpo, headers) é idêntica à do backend bloqueante, sem mudança de contrato observável
