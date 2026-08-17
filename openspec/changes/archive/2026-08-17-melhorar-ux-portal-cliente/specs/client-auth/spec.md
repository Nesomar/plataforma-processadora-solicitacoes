## ADDED Requirements

### Requirement: Cadastro exige email em formato válido
O sistema SHALL validar que o email informado no cadastro segue um formato válido de endereço de
email antes de criar a credencial.

#### Scenario: Email em formato inválido
- **WHEN** o cliente tenta se cadastrar com um valor que não é um email válido (ex: sem `@` ou
  domínio)
- **THEN** o sistema rejeita o cadastro sem criar a credencial

#### Scenario: Email em formato válido
- **WHEN** o cliente se cadastra com um email em formato válido e ainda não utilizado
- **THEN** o sistema segue o fluxo normal de cadastro
