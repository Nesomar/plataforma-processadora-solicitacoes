## ADDED Requirements

### Requirement: Cliente possui no máximo uma solicitação ativa
O sistema SHALL impedir que o cliente tenha mais de uma solicitação com status `ABERTA`
simultaneamente: ao solicitar a criação de uma nova solicitação enquanto já existe uma `ABERTA`,
o sistema SHALL retornar a solicitação `ABERTA` existente em vez de criar outra.

#### Scenario: Cliente sem solicitação aberta cria uma nova
- **WHEN** um cliente com perfil completo e sem nenhuma solicitação `ABERTA` solicita a criação
  de uma nova solicitação
- **THEN** o sistema cria uma nova solicitação com status `ABERTA`

#### Scenario: Cliente com solicitação aberta tenta criar outra
- **WHEN** um cliente que já possui uma solicitação com status `ABERTA` solicita a criação de uma
  nova solicitação
- **THEN** o sistema retorna a solicitação `ABERTA` já existente, sem criar uma nova

### Requirement: Cliente edita solicitação em aberto
O sistema SHALL permitir que o cliente edite os dados (dados pessoais, endereço, renda) de uma
solicitação própria enquanto ela estiver com status `ABERTA`, sem afetar os dados do perfil do
cliente.

#### Scenario: Cliente edita solicitação aberta
- **WHEN** o cliente autenticado envia uma atualização para uma solicitação própria com status
  `ABERTA`
- **THEN** o sistema atualiza os dados da solicitação e mantém o status `ABERTA`

#### Scenario: Cliente tenta editar solicitação de outro cliente
- **WHEN** um cliente autenticado tenta editar uma solicitação que pertence a outro cliente
- **THEN** o sistema nega o acesso
