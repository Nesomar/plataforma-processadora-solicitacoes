# Infra local (ministack)

```bash
docker compose up --build
```

Sobe tudo automatizado, nessa ordem (via `depends_on` + healthcheck/`service_completed_successfully`):

1. **ministack** — [emulador AWS](https://github.com/ministackorg/ministack) na porta `4566` (DynamoDB, S3, SQS, API Gateway).
2. **terraform-init** — container one-shot (`infra/terraform/docker-init-apply.sh`) que gera `infra/terraform/override.tf`
   (provider com endpoints apontados pro ministack — não versionado, seção seguinte explica o conteúdo), roda
   `terraform init` + `apply` só dos módulos suportados localmente, e grava os outputs em
   `infra/terraform/env/backend.env` e `infra/terraform/env/frontend.env` (gitignored).
3. **backend** / **frontend** — sobem em container só depois do `terraform-init` terminar com sucesso, lendo os `.env`
   gerados no passo anterior via `env_file` no `docker-compose.yml`.

Como `network`/`api_gateway`/`ecs` dependem de NLB + VPC Link (gap conhecido, seção abaixo), o `terraform apply` do
`terraform-init` usa `-target` pra só subir `dynamodb`/`s3`/`sqs` — os módulos sem suporte confirmado no
ministack ficam de fora.

O `override.tf` gerado (efêmero, recriado a cada `docker compose up`):

```hcl
provider "aws" {
  access_key                  = "test"
  secret_key                  = "test"
  s3_use_path_style           = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    apigatewayv2 = "http://ministack:4566"   # nome do serviço docker-compose, não localhost —
    dynamodb     = "http://ministack:4566"   # terraform-init roda em container na mesma rede
    ecs          = "http://ministack:4566"
    iam          = "http://ministack:4566"
    s3           = "http://ministack:4566"
    sqs          = "http://ministack:4566"
    sts          = "http://ministack:4566"
    ec2          = "http://ministack:4566"
    elbv2        = "http://ministack:4566"
    logs         = "http://ministack:4566"
  }
}
```

O backend emite e valida seu próprio JWT (HS256, `JWT_SIGNING_SECRET`) — sem serviço externo
envolvido, nem emulado nem real. `docker-init-apply.sh` grava um segredo fixo pra dev local (mesmo
valor do default em `application.yml`), então login funciona sem nenhum ajuste manual entre
rebuilds — era exatamente esse tipo de sincronização manual (IDs/segredos recriados a cada
`docker compose up --build`) que existia com o Cognito e motivou a remoção (ver
`openspec/changes/archive/*/remover-cognito-auth-propria/design.md`).

Sem `AWS_SQS_ATTACHMENTS_QUEUE_URL` setada, todo upload de anexo falha no passo de publicar na fila
(queueUrl vazio). O `docker-init-apply.sh` cobre isso.

## Rodando backend/frontend fora de container (loop de dev mais rápido)

Depois de rodar `docker compose up` pelo menos uma vez (gera `infra/terraform/env/*.env` com
`ministack:4566` — nome só resolvível na rede docker, por isso o `sed` abaixo troca por `localhost`):

```bash
cd backend
set -a && . <(sed 's/ministack:4566/localhost:4566/;s#http://ministack:4566/#http://localhost:4566/#' ../infra/terraform/env/backend.env) && set +a
./gradlew bootRun
```

```powershell
Get-Content ..\infra\terraform\env\backend.env | ForEach-Object {
  if ($_ -match '^([^=]+)=(.*)$') { Set-Item "env:$($Matches[1])" ($Matches[2] -replace 'ministack:4566', 'localhost:4566') }
}
./gradlew bootRun
```

Ou, mais simples: rode o backend também via `docker compose up backend` e itere só no frontend fora de container.

## Gap conhecido: NLB + VPC Link (task 1.9, spike)

`modules/network` cria um NLB e `modules/api_gateway` cria um VPC Link privado apontando pro listener desse NLB (é assim que o API Gateway HTTP API chega no ECS em produção). Pesquisa na documentação do ministack (ago/2026) mostra:

- **ELBv2**: suporte documentado é focado em ALB (roteamento por `path-pattern`/`host-header`/regras L7 para Lambda). Não há confirmação de que o tipo `network` (NLB) tenha paridade real de data-plane.
- **VPC Link** (`aws_apigatewayv2_vpc_link`): não aparece em nenhum lugar da documentação/lista de serviços suportados.

**Conclusão do spike:** o caminho API Gateway → VPC Link → NLB → ECS não tem suporte confirmado no ministack. Conforme já previsto no `design.md` (Risks/Trade-offs), esse trecho específico só é validado contra AWS real (fase 6 — deploy). Localmente, os módulos `network`/`api_gateway`/`ecs` podem ser aplicados para validar a parte que roda em cada serviço individualmente (ex: ECS via `RunTask` real com Docker socket, DynamoDB, S3, SQS), mas o roteamento ponta-a-ponta via API Gateway não é confiável localmente até esse gap ser fechado (upstream) ou reavaliado.
