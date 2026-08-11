# service-requests

## Purpose

Criação de novas solicitações pelo cliente (reaproveitando os dados já cadastrados no perfil) e listagem/acompanhamento restrito às solicitações da própria conta.

## Requirements

### Requirement: Nova solicitação exige perfil completo
O sistema SHALL exigir que o perfil do cliente esteja completo (onboarding concluído) antes de permitir a criação de uma nova solicitação.

#### Scenario: Cliente com perfil incompleto tenta criar solicitação
- **WHEN** um cliente cujo perfil não está completo tenta criar uma nova solicitação
- **THEN** o sistema rejeita a criação e direciona o cliente para concluir o onboarding

### Requirement: Nova solicitação reaproveita dados do perfil
Ao criar uma nova solicitação, o sistema SHALL reaproveitar os dados já cadastrados no perfil do cliente (dados pessoais, endereço, renda), sem exigir que sejam preenchidos novamente.

#### Scenario: Cliente cria solicitação após cadastro completo
- **WHEN** um cliente com perfil completo cria uma nova solicitação
- **THEN** o sistema associa a solicitação aos dados de perfil já existentes, sem solicitar reenvio de endereço, dados pessoais ou renda

### Requirement: Cliente acompanha suas próprias solicitações
O sistema SHALL permitir que o cliente liste e acompanhe apenas as solicitações associadas à sua própria conta.

#### Scenario: Cliente lista suas solicitações
- **WHEN** um cliente autenticado acessa o dashboard
- **THEN** o sistema retorna somente as solicitações pertencentes a esse cliente, com seus respectivos status

#### Scenario: Cliente não acessa solicitação de outro cliente
- **WHEN** um cliente autenticado tenta consultar uma solicitação que pertence a outro cliente
- **THEN** o sistema nega o acesso
