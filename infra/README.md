# Infra local (ministack)

```bash
docker compose up -d ministack
```

Sobe o [ministack](https://github.com/ministackorg/ministack) na porta única `4566`, emulando DynamoDB, S3, SQS, Cognito e API Gateway (REST e HTTP API) para desenvolvimento sem conta AWS real.

Pra aplicar localmente com CORS liberado pro Vite dev server, passe `-var 'local_dev_origins=["http://localhost:5173"]'` (vazio por padrão para não vazar em prod/staging).

Para apontar o Terraform pro ministack local em vez da AWS real, use um provider com endpoints customizados e credenciais dummy:

```hcl
provider "aws" {
  region                      = "sa-east-1"
  access_key                  = "test"
  secret_key                  = "test"
  s3_use_path_style           = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_key = true

  endpoints {
    apigatewayv2 = "http://localhost:4566"
    cognitoidp   = "http://localhost:4566"
    dynamodb     = "http://localhost:4566"
    ecs          = "http://localhost:4566"
    iam          = "http://localhost:4566"
    s3           = "http://localhost:4566"
    sqs          = "http://localhost:4566"
    sts          = "http://localhost:4566"
    ec2          = "http://localhost:4566"
    elbv2        = "http://localhost:4566"
    logs         = "http://localhost:4566"
  }
}
```

## Rodando o backend local contra o ministack

Depois de aplicar o Terraform contra o ministack (seção acima) e pegar os outputs, exporte:

```bash
export COGNITO_ISSUER_URI=$(terraform output -raw cognito_issuer_url)
export AWS_REGION=sa-east-1
export AWS_ENDPOINT_OVERRIDE=http://localhost:4566
export AWS_DYNAMODB_TABLE_NAME=$(terraform output -raw dynamodb_table_name)
export AWS_S3_ATTACHMENTS_BUCKET=$(terraform output -raw attachments_bucket_name)
export AWS_SQS_ATTACHMENTS_QUEUE_URL=$(terraform output -raw attachments_queue_url)
```

Sem `AWS_SQS_ATTACHMENTS_QUEUE_URL` setada, todo upload de anexo falha no passo de publicar na fila (queueUrl vazio).

## Gap conhecido: NLB + VPC Link (task 1.9, spike)

`modules/network` cria um NLB e `modules/api_gateway` cria um VPC Link privado apontando pro listener desse NLB (é assim que o API Gateway HTTP API chega no ECS em produção). Pesquisa na documentação do ministack (ago/2026) mostra:

- **ELBv2**: suporte documentado é focado em ALB (roteamento por `path-pattern`/`host-header`/regras L7 para Lambda). Não há confirmação de que o tipo `network` (NLB) tenha paridade real de data-plane.
- **VPC Link** (`aws_apigatewayv2_vpc_link`): não aparece em nenhum lugar da documentação/lista de serviços suportados.

**Conclusão do spike:** o caminho API Gateway → VPC Link → NLB → ECS não tem suporte confirmado no ministack. Conforme já previsto no `design.md` (Risks/Trade-offs), esse trecho específico só é validado contra AWS real (fase 6 — deploy). Localmente, os módulos `network`/`api_gateway`/`ecs` podem ser aplicados para validar a parte que roda em cada serviço individualmente (ex: ECS via `RunTask` real com Docker socket, DynamoDB, S3, SQS, Cognito), mas o roteamento ponta-a-ponta via API Gateway não é confiável localmente até esse gap ser fechado (upstream) ou reavaliado.
