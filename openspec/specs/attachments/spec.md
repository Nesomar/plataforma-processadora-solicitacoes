# attachments

## Purpose

Upload de anexos do cliente (etapa de anexos do onboarding) intermediado pelo backend (ECS), com gravação no S3 e publicação de evento em fila SQS, sem validação de conteúdo nesta fase.

## Requirements

### Requirement: Upload de anexo passa pelo backend
O sistema SHALL receber o arquivo enviado pelo cliente através do ECS (não via URL pré-assinada direto para o S3), mantendo o backend no caminho de todo upload.

#### Scenario: Cliente envia anexo
- **WHEN** o cliente envia um arquivo na etapa de anexos do onboarding
- **THEN** o arquivo é transmitido para o ECS, que é responsável por gravá-lo no S3

### Requirement: Anexo gravado no S3 e evento publicado na fila
Após receber o arquivo, o backend SHALL gravar o objeto no S3 e publicar um evento na fila SQS referenciando o anexo, mesmo sem processamento adicional nesta fase.

#### Scenario: Upload bem-sucedido gera evento na fila
- **WHEN** o ECS grava o anexo com sucesso no S3
- **THEN** o sistema registra a metadata do anexo no DynamoDB e publica um evento correspondente na fila SQS

#### Scenario: Falha ao gravar no S3 não gera evento
- **WHEN** a gravação no S3 falha
- **THEN** o sistema não publica evento na fila e retorna erro ao cliente, permitindo reenvio

### Requirement: Sem validação de conteúdo no MVP
O sistema SHALL aceitar arquivos PDF sem validação de conteúdo (scan de antivírus ou verificação aprofundada) nesta fase, apenas registrando o anexo recebido.

#### Scenario: PDF simples é aceito sem verificação de conteúdo
- **WHEN** o cliente envia um arquivo PDF na etapa de anexos
- **THEN** o sistema aceita e armazena o arquivo sem executar verificação de conteúdo além de checagens básicas de formato

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
