## ADDED Requirements

### Requirement: Cliente visualiza os próprios anexos enviados
O sistema SHALL permitir que o cliente liste e visualize (sem opção de download) os anexos que já
enviou, restrito aos anexos da própria conta.

#### Scenario: Cliente lista seus anexos
- **WHEN** o cliente autenticado acessa a lista de anexos enviados
- **THEN** o sistema retorna somente os anexos associados à própria conta

#### Scenario: Cliente visualiza um anexo
- **WHEN** o cliente autenticado abre um anexo próprio para visualização
- **THEN** o sistema exibe o conteúdo do arquivo inline, sem oferecer opção de download

#### Scenario: Cliente não acessa anexo de outro cliente
- **WHEN** um cliente autenticado tenta visualizar um anexo que pertence a outro cliente
- **THEN** o sistema nega o acesso
