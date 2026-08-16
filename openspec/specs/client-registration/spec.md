# client-registration

## Purpose

Autocadastro do cliente final (self sign-up) via credenciais próprias (email + senha),
com a conta ativa imediatamente após o cadastro — sem etapa de confirmação por email.

## Requirements

### Requirement: Autocadastro do cliente

O sistema SHALL permitir que um cliente crie a própria conta informando email e senha, sem
depender de criação manual de usuário. A conta fica ativa imediatamente após o cadastro.

#### Scenario: Cadastro com dados válidos

- **WHEN** o cliente preenche email e senha válidos e envia o formulário de cadastro
- **THEN** o sistema cria a credencial, associa um novo `clienteId`, e a conta fica pronta
  para login imediatamente

#### Scenario: Cadastro com email já existente

- **WHEN** o cliente tenta se cadastrar com um email que já possui conta
- **THEN** o sistema rejeita o cadastro e exibe mensagem indicando que o email já está em uso,
  sem sobrescrever a credencial existente
