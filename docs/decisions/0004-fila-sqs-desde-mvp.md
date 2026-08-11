# 4. Fila SQS para anexos desde o MVP, mesmo sem validação de conteúdo

**Status:** aceito

## Contexto

Anexos aceitos sem scan de conteúdo nesta fase (PDFs simples). Não há hoje nenhum trabalho pesado
pra tirar da request síncrona — a fila não resolve um problema de performance existente.

## Decisão

`AnexoService` publica evento na SQS (`SqsEventoAnexoPublisher`) a cada upload bem-sucedido, mesmo
sem nenhum consumidor real do outro lado ainda.

## Raciocínio

Upgrade-path deliberado: quando entrar validação/antivírus, é só plugar um worker na fila já
existente — sem redesenhar o fluxo de upload. Troca consciente: menos simples agora (fila sem
consumidor), evita retrabalho de redesenhar o endpoint de upload depois.

## Trade-off aceito

Fila sem validação aceita qualquer PDF, incluindo malicioso, até que um worker de scan seja
adicionado. Aceito conscientemente pro MVP.
