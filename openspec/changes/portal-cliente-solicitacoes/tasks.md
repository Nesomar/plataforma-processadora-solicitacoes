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

- [ ] 3.1 Domain: modelo de Perfil (dados pessoais, endereço, renda) com estado de draft por etapa
- [ ] 3.2 Use case: salvar etapa (PATCH incremental) com validação de ordem sequencial no backend
- [ ] 3.3 Use case: consultar gate de perfil completo (GetItem DynamoDB)
- [ ] 3.4 Adapter-in: endpoints REST das etapas do wizard
- [ ] 3.5 Frontend: wizard multi-etapa (dados pessoais → endereço → renda), com retomada a partir do draft salvo
- [ ] 3.6 Frontend: redirecionamento pós-login baseado no gate de perfil completo
- [ ] 3.7 Testes: retomada de cadastro no meio; bloqueio de pular etapa via API direta; gate correto para perfil completo/incompleto (cobre specs/client-profile/spec.md)
- [ ] 3.8 Code review da etapa

## 4. attachments — upload de anexos (branch `feature/attachments`)

- [ ] 4.1 Domain: modelo de Anexo (metadata, S3 key, status)
- [ ] 4.2 Use case: receber upload via ECS, gravar no S3, publicar evento na fila SQS
- [ ] 4.3 Adapter-out: cliente S3 e cliente SQS
- [ ] 4.4 Adapter-in: endpoint REST de upload (etapa de anexos do wizard)
- [ ] 4.5 Frontend: etapa de anexos no wizard (seleção/envio de PDF)
- [ ] 4.6 Testes: upload grava no S3 e publica evento; falha na gravação não publica evento (cobre specs/attachments/spec.md)
- [ ] 4.7 Code review da etapa

## 5. service-requests — nova solicitação e acompanhamento (branch `feature/service-requests`)

- [ ] 5.1 Domain: modelo de Solicitação, associada ao Cliente
- [ ] 5.2 Use case: criar solicitação reaproveitando dados do perfil, bloqueando se perfil incompleto
- [ ] 5.3 Use case: listar solicitações do próprio cliente
- [ ] 5.4 Adapter-in: endpoints REST de criação/listagem
- [ ] 5.5 Frontend: dashboard (lista de solicitações + ação "nova solicitação")
- [ ] 5.6 Frontend: tela de acompanhamento de status da solicitação
- [ ] 5.7 Testes: criação bloqueada com perfil incompleto; reaproveitamento de dados do perfil; cliente não acessa solicitação de outro cliente (cobre specs/service-requests/spec.md)
- [ ] 5.8 Code review da etapa

## 6. Deploy real na AWS (branch `feature/deploy-aws`)

- [ ] 6.1 Aplicar Terraform no ambiente AWS real (conta de dev/staging)
- [ ] 6.2 Build e deploy do frontend no S3 + invalidação CloudFront
- [ ] 6.3 Build e deploy da imagem do backend no ECS
- [ ] 6.4 Smoke test ponta a ponta contra AWS real (login → onboarding → anexo → solicitação)
- [ ] 6.5 Comparar comportamento observado com o validado localmente via ministack; documentar divergências
- [ ] 6.6 Code review da etapa
