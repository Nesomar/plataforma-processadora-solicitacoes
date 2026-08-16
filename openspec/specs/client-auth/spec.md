# client-auth

## Purpose

Cadastro e login do cliente final, com emissão e validação de JWT pelo próprio backend
(HS256, segredo simétrico) — sem Cognito nem qualquer validador externo.

## Requirements

### Requirement: Cadastro de novo cliente
O sistema SHALL permitir que um novo cliente se cadastre com email e senha, armazenando a
credencial (email + hash da senha) no backend. A conta SHALL ficar ativa imediatamente após o
cadastro, sem etapa de confirmação por e-mail.

#### Scenario: Cadastro com e-mail ainda não utilizado
- **WHEN** o cliente envia um email não cadastrado e uma senha
- **THEN** o sistema cria a credencial, associa um novo `clienteId`, e a conta fica pronta para
  login imediatamente

#### Scenario: Cadastro com e-mail já cadastrado
- **WHEN** o cliente tenta se cadastrar com um email que já possui credencial
- **THEN** o sistema rejeita o cadastro sem sobrescrever a credencial existente

### Requirement: Login do cliente
O sistema SHALL autenticar o cliente através de credenciais próprias (email + senha) armazenadas
no backend, emitindo um JWT assinado pelo próprio backend em caso de credenciais válidas.

#### Scenario: Login com credenciais válidas
- **WHEN** o cliente envia email e senha válidos
- **THEN** o sistema retorna um JWT assinado pelo backend, com a claim `sub` igual ao `clienteId`

#### Scenario: Login com credenciais inválidas
- **WHEN** o cliente envia email ou senha incorretos
- **THEN** o sistema rejeita a autenticação sem emitir token

### Requirement: Backend revalida a assinatura do token
O backend SHALL validar a assinatura do JWT recebido em toda requisição autenticada, usando a
chave própria de assinatura (não há mais Cognito Authorizer ou qualquer outro validador upstream).

#### Scenario: Backend aceita token válido
- **WHEN** o backend recebe uma requisição com um JWT íntegro, assinatura válida e não expirado
- **THEN** o backend processa a requisição normalmente

#### Scenario: Backend rejeita token com assinatura inválida
- **WHEN** o backend recebe uma requisição cujo JWT tem assinatura que não confere com a chave de
  assinatura do backend, ou está expirado
- **THEN** o backend rejeita a requisição com erro de autenticação
