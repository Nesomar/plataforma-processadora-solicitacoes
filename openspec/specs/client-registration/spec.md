# client-registration

## Purpose

Autocadastro do cliente final (self sign-up) via Amazon Cognito, com confirmação de conta por
código enviado por email, antes de liberar o login.

## Requirements

### Requirement: Autocadastro do cliente via Cognito
O sistema SHALL permitir que um cliente crie a própria conta informando email e senha, usando o fluxo de self sign-up do Amazon Cognito (ou emulação equivalente via ministack em ambiente local), sem depender de criação manual de usuário.

#### Scenario: Cadastro com dados válidos
- **WHEN** o cliente preenche email e senha válidos (conforme a política de senha do user pool) e envia o formulário de cadastro
- **THEN** o sistema cria o usuário no Cognito em estado não confirmado e dispara o envio de um código de confirmação para o email informado

#### Scenario: Cadastro com email já existente
- **WHEN** o cliente tenta se cadastrar com um email que já possui conta no Cognito
- **THEN** o sistema rejeita o cadastro e exibe mensagem indicando que o email já está em uso, sem criar duplicata

#### Scenario: Cadastro com senha fora da política
- **WHEN** o cliente informa uma senha que não atende a política do user pool (mínimo 8 caracteres, com maiúscula, minúscula e número)
- **THEN** o sistema impede o envio e exibe o motivo antes de chamar o Cognito

### Requirement: Confirmação de cadastro por código de email
O sistema SHALL exigir que o cliente confirme a conta recém-criada informando o código enviado por email antes de permitir login.

#### Scenario: Confirmação com código correto
- **WHEN** o cliente informa o código de confirmação correto recebido por email
- **THEN** o sistema confirma a conta no Cognito e redireciona o cliente para a tela de login

#### Scenario: Confirmação com código incorreto ou expirado
- **WHEN** o cliente informa um código de confirmação inválido ou expirado
- **THEN** o sistema rejeita a confirmação, mantém a conta não confirmada e exibe mensagem de erro sem permitir login
