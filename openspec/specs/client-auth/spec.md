# client-auth

## Purpose

Login do cliente final, com emissão e validação de JWT via Amazon Cognito (API Gateway com Cognito Authorizer) e revalidação da assinatura no backend (ECS).

## Requirements

### Requirement: Login do cliente via Cognito
O sistema SHALL autenticar o cliente através do Amazon Cognito (ou emulação equivalente via ministack em ambiente local), emitindo um JWT em caso de credenciais válidas.

#### Scenario: Login com credenciais válidas
- **WHEN** o cliente envia email e senha válidos
- **THEN** o sistema retorna um JWT (id/access token) emitido pelo Cognito

#### Scenario: Login com credenciais inválidas
- **WHEN** o cliente envia email ou senha incorretos
- **THEN** o sistema rejeita a autenticação sem emitir token

### Requirement: API Gateway valida o token antes de rotear
O API Gateway SHALL validar o JWT via Cognito Authorizer antes de encaminhar qualquer requisição para o backend.

#### Scenario: Requisição sem token válido é bloqueada no gateway
- **WHEN** uma requisição chega ao API Gateway sem token, com token expirado ou com assinatura inválida
- **THEN** o API Gateway rejeita a requisição antes de alcançar o ECS

### Requirement: Backend revalida a assinatura do token
O backend (ECS) SHALL revalidar a assinatura do JWT recebido, não confiando apenas na validação feita pelo API Gateway.

#### Scenario: Backend aceita token válido já validado pelo gateway
- **WHEN** o ECS recebe uma requisição com um JWT íntegro e assinatura válida
- **THEN** o backend processa a requisição normalmente

#### Scenario: Backend rejeita token com assinatura inválida
- **WHEN** o ECS recebe uma requisição cujo JWT tem assinatura que não confere com a chave pública do Cognito
- **THEN** o backend rejeita a requisição com erro de autenticação, independente do que o gateway tenha permitido
