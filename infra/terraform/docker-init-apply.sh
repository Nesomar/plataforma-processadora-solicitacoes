#!/bin/sh
set -eu

cat > override.tf <<'EOF'
provider "aws" {
  access_key                  = "test"
  secret_key                  = "test"
  s3_use_path_style           = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    apigatewayv2 = "http://ministack:4566"
    cognitoidp   = "http://ministack:4566"
    dynamodb     = "http://ministack:4566"
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
EOF

terraform init -input=false
terraform apply -auto-approve -input=false \
  -target=module.dynamodb -target=module.s3.aws_s3_bucket.attachments -target=module.sqs \
  -var 'local_dev_origins=["http://localhost:5173"]'

mkdir -p env

# Segredo fixo pra dev local (não é segredo de produção real — simplicidade > segurança nesse
# ambiente descartável; ver design.md decisão 1). Mesmo valor do default em application.yml,
# só explícito aqui pra deixar claro que backend e frontend sobem com auth funcionando sem
# nenhum ajuste manual, idempotente entre rebuilds (era o problema original com o Cognito).
cat > env/backend.env <<EOF
JWT_SIGNING_SECRET=dev-local-insecure-jwt-signing-secret-troque-em-producao
AWS_REGION=sa-east-1
AWS_ENDPOINT_OVERRIDE=http://ministack:4566
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test
AWS_DYNAMODB_TABLE_NAME=$(terraform output -raw dynamodb_table_name)
AWS_S3_ATTACHMENTS_BUCKET=$(terraform output -raw attachments_bucket_name)
AWS_SQS_ATTACHMENTS_QUEUE_URL=$(terraform output -raw attachments_queue_url)
CORS_ALLOWED_ORIGINS=http://localhost:5173
EOF

cat > env/frontend.env <<EOF
VITE_API_BASE_URL=http://localhost:8080
EOF

echo "env/backend.env e env/frontend.env gerados."
