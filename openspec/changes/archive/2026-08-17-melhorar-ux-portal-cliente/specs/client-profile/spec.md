## ADDED Requirements

### Requirement: Validação de formato dos campos de dados pessoais e endereço
O sistema SHALL validar o formato de CPF (11 dígitos com dígito verificador válido), CEP (8
dígitos) e telefone (DDD + 8 ou 9 dígitos) antes de persistir os dados pessoais e endereço do
cliente, rejeitando valores com formato inválido mesmo que não estejam em branco.

#### Scenario: CPF com dígito verificador inválido
- **WHEN** o cliente envia um CPF com 11 dígitos mas dígito verificador incorreto
- **THEN** o sistema rejeita a requisição sem persistir o dado

#### Scenario: CEP fora do formato
- **WHEN** o cliente envia um CEP que não tem 8 dígitos numéricos
- **THEN** o sistema rejeita a requisição sem persistir o dado

#### Scenario: Telefone fora do formato
- **WHEN** o cliente envia um telefone sem DDD ou com quantidade de dígitos inválida
- **THEN** o sistema rejeita a requisição sem persistir o dado

#### Scenario: Campos com formato válido são aceitos
- **WHEN** o cliente envia CPF, CEP e telefone em formato válido
- **THEN** o sistema aceita e persiste os dados normalmente
