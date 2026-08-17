# client-profile

## Purpose

Wizard de onboarding sequencial do cliente (dados pessoais, endereço, renda, anexos) com persistência parcial por etapa, e o gate que decide se o cliente é direcionado ao wizard ou ao dashboard com base no estado real do perfil.

## Requirements

### Requirement: Onboarding sequencial por etapas
O sistema SHALL apresentar o cadastro complementar do cliente (dados pessoais, endereço, renda, anexos) como um wizard de etapas sequenciais, sem permitir avançar para uma etapa sem concluir a anterior.

#### Scenario: Cliente tenta pular etapa
- **WHEN** o cliente tenta enviar dados da etapa de renda sem ter concluído a etapa de endereço
- **THEN** o sistema rejeita a requisição, mesmo que a chamada venha direto da API (sem passar pelo front)

#### Scenario: Cliente avança em ordem
- **WHEN** o cliente conclui a etapa de dados pessoais
- **THEN** o sistema libera o acesso à etapa de endereço

### Requirement: Persistência parcial (draft) por etapa
O sistema SHALL persistir os dados de cada etapa imediatamente após o envio (PATCH incremental), permitindo que o cliente retome o cadastro de onde parou.

#### Scenario: Cliente sai no meio do cadastro e retorna depois
- **WHEN** o cliente concluiu as etapas de dados pessoais e endereço, fecha a sessão, e faz login novamente depois
- **THEN** o sistema exibe o wizard já na etapa de renda, com os dados anteriores preservados

### Requirement: Gate de perfil completo via consulta ao banco
O sistema SHALL determinar se o cliente deve ser direcionado ao wizard de onboarding ou ao dashboard consultando o estado do perfil no DynamoDB a cada login/requisição relevante, não através de um claim armazenado no JWT.

#### Scenario: Cliente com perfil incompleto faz login
- **WHEN** um cliente cujo perfil não está completo (etapas pendentes) faz login
- **THEN** o sistema o direciona para o wizard, na etapa pendente correta

#### Scenario: Cliente com perfil completo faz login
- **WHEN** um cliente cujo perfil está completo faz login
- **THEN** o sistema o direciona diretamente para o dashboard, sem passar pelo wizard

#### Scenario: Perfil completado durante a sessão reflete imediatamente
- **WHEN** o cliente conclui a última etapa do wizard dentro da mesma sessão (mesmo token ainda válido)
- **THEN** a próxima verificação de perfil completo já retorna "completo", sem exigir novo login ou refresh de token

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
