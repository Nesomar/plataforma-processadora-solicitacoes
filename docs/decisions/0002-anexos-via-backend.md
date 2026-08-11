# 2. Upload de anexo passa pelo backend, não presigned URL

**Status:** aceito

## Contexto

Cliente precisa enviar PDF na etapa de anexos do wizard. Duas formas comuns: presigned URL
(cliente sobe direto pro S3) ou o upload passar pelo backend.

## Decisão

Upload vai pro ECS primeiro; o backend grava no S3 e publica evento na fila SQS
(`AnexoService.enviarAnexo`).

## Alternativa descartada

Presigned URL direto do cliente pro S3. Mais barato e rápido (tira carga do backend), mas tira o
controle do backend sobre o que entra no bucket — mesmo sem validar conteúdo ainda no MVP, manter
esse controle é o objetivo (é o gancho pra validação/antivírus entrar depois sem redesenho).

## Trade-off aceito

Sem validação de conteúdo (scan de antivírus) nesta fase — só checagem básica de formato
(content-type `application/pdf`). Ver [`0004-fila-sqs-desde-mvp.md`](0004-fila-sqs-desde-mvp.md).
