# 6. Sem deploy real em conta AWS

**Status:** aceito

## Contexto

O plano original (fase 6) previa aplicar o Terraform numa conta AWS de dev/staging e rodar smoke
test de ponta a ponta contra ela, validando paridade com o ambiente local (ministack).

## Decisão

Deploy real ficou fora de escopo. A infra Terraform (`infra/terraform/`) continua existindo e é
AWS-alvo — fica pronta pra quem for aplicar — mas não foi executada neste projeto.

## Consequência

Um gap específico fica sem validação prática: suporte do ministack a NLB/VPC Link não é confirmado
(pesquisa na documentação não achou confirmação — ver `infra/README.md`), e sem deploy real esse
trecho do roteamento (API Gateway → VPC Link → NLB → ECS) nunca foi testado de fato, só escrito.
Todo o resto (Cognito, DynamoDB, S3, SQS, lógica de negócio) foi validado localmente via ministack
e testes automatizados.
