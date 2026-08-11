## 1. Infra base e ambiente local (branch `feature/infra-base`)

- [x] 1.1 Módulo Terraform de rede: VPC, subnets, NLB
- [x] 1.2 Módulo Terraform: Cognito User Pool
- [x] 1.3 Módulo Terraform: tabela DynamoDB (single-table, PK/SK conforme design.md)
- [x] 1.4 Módulo Terraform: buckets S3 (frontend estático + anexos)
- [x] 1.5 Módulo Terraform: fila SQS de anexos
- [x] 1.6 Módulo Terraform: API Gateway HTTP API + VPC Link apontando para o NLB
- [x] 1.7 Módulo Terraform: ECS cluster/service/task-def (esqueleto, sem app ainda) + IAM roles
- [x] 1.8 docker-compose com ministack cobrindo ECS/API Gateway/DynamoDB/S3/SQS/Cognito
- [x] 1.9 Validar suporte do ministack a NLB/VPC Link (spike — ver Open Questions em design.md); documentar gap se houver
- [x] 1.10 Code review da etapa

## 2. Backend esqueleto + client-auth (branch `feature/client-auth`)

- [x] 2.1 Esqueleto do projeto Kotlin/Spring Boot com estrutura hexagonal (domain / ports / adapters-in / adapters-out)
- [x] 2.2 Adapter-out DynamoDB (repositório base, conexão configurável via endpoint — ministack local ou AWS real)
- [x] 2.3 Integração Spring Security resource server: revalidação de assinatura do JWT do Cognito
- [x] 2.4 Configurar Cognito Authorizer no API Gateway (Terraform)
- [x] 2.5 Frontend: tela de login (React) integrada ao Cognito
- [x] 2.6 Frontend: interceptor de requisições anexando Bearer token
- [x] 2.7 Testes: login com credenciais válidas/inválidas; requisição sem token rejeitada no gateway; token com assinatura inválida rejeitado no backend (cobre specs/client-auth/spec.md)
- [x] 2.8 Code review da etapa

## 3. client-profile — onboarding sequencial (branch `feature/client-profile`)

- [x] 3.1 Domain: modelo de Perfil (dados pessoais, endereço, renda) com estado de draft por etapa
- [x] 3.2 Use case: salvar etapa (PATCH incremental) com validação de ordem sequencial no backend
- [x] 3.3 Use case: consultar gate de perfil completo (GetItem DynamoDB)
- [x] 3.4 Adapter-in: endpoints REST das etapas do wizard
- [x] 3.5 Frontend: wizard multi-etapa (dados pessoais → endereço → renda), com retomada a partir do draft salvo
- [x] 3.6 Frontend: redirecionamento pós-login baseado no gate de perfil completo
- [x] 3.7 Testes: retomada de cadastro no meio; bloqueio de pular etapa via API direta; gate correto para perfil completo/incompleto (cobre specs/client-profile/spec.md)
- [x] 3.8 Code review da etapa

## 4. attachments — upload de anexos (branch `feature/attachments`)

- [x] 4.1 Domain: modelo de Anexo (metadata, S3 key, status)
- [x] 4.2 Use case: receber upload via ECS, gravar no S3, publicar evento na fila SQS
- [x] 4.3 Adapter-out: cliente S3 e cliente SQS
- [x] 4.4 Adapter-in: endpoint REST de upload (etapa de anexos do wizard)
- [x] 4.5 Frontend: etapa de anexos no wizard (seleção/envio de PDF)
- [x] 4.6 Testes: upload grava no S3 e publica evento; falha na gravação não publica evento (cobre specs/attachments/spec.md)
- [x] 4.7 Code review da etapa

## 5. service-requests — nova solicitação e acompanhamento (branch `feature/service-requests`)

- [x] 5.1 Domain: modelo de Solicitação, associada ao Cliente
- [x] 5.2 Use case: criar solicitação reaproveitando dados do perfil, bloqueando se perfil incompleto
- [x] 5.3 Use case: listar solicitações do próprio cliente
- [x] 5.4 Adapter-in: endpoints REST de criação/listagem
- [x] 5.5 Frontend: dashboard (lista de solicitações + ação "nova solicitação")
- [x] 5.6 Frontend: tela de acompanhamento de status da solicitação
- [x] 5.7 Testes: criação bloqueada com perfil incompleto; reaproveitamento de dados do perfil; cliente não acessa solicitação de outro cliente (cobre specs/service-requests/spec.md)
- [x] 5.8 Code review da etapa

