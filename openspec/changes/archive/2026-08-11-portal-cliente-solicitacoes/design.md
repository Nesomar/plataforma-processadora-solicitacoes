## Context

Projeto greenfield (repositório vazio). Decisões abaixo saíram de uma sessão de exploração (`/opsx:explore`) antes desta proposta — já são compromissos fechados, não hipóteses.

Stack: React (frontend) + Kotlin/Spring Boot mais recentes (backend, arquitetura hexagonal) + DynamoDB. Infra AWS: API Gateway na frente de ECS Fargate. Local: docker-compose + [ministack](https://ministack.org/) (emulador AWS MIT, porta única 4566, cobre ECS/API Gateway/DynamoDB/S3/SQS/Cognito). Provisionamento real via Terraform. Cada etapa de desenvolvimento em branch `feature/{nome-da-funcionalidade}`, com code review ao final de cada etapa.

## Goals / Non-Goals

**Goals:**
- Cliente loga, completa cadastro por etapas (retomável), acompanha e cria solicitações reaproveitando o perfil.
- Arquitetura hexagonal no backend, single-table DynamoDB, paridade local via ministack.
- Infra 100% via Terraform, sem clique manual no console AWS.

**Non-Goals:**
- Validação/scan de conteúdo de anexos (fica para uma fase futura — a fila SQS já é o gancho para isso).
- Consulta de solicitações por operador/backoffice (só o próprio cliente consulta, nesta fase).
- Qualquer fluxo de aprovação/análise da solicitação em si (fora de escopo desta proposta — cobre só criar e acompanhar).

## Decisions

**Onboarding sequencial com draft incremental.** Cada etapa (dados pessoais → endereço → renda → anexos) faz PATCH imediato de um registro `PROFILE` em rascunho. Front bloqueia pular etapa, mas backend também valida a ordem (não confia só no front) — evita que alguém chame a API direto e pule etapa obrigatória. Alternativa descartada: salvar tudo só no fim (formulário único) — perde retomada e não combina com "processo por etapas" pedido.

**Gate "perfil completo" via consulta DynamoDB, não claim no JWT.** A cada login/request decisivo, um `GetItem` barato confere se o perfil está completo. Alternativa descartada: custom claim no token — mais barato em teoria, mas fica desatualizado até o refresh do token (cliente completa cadastro e ainda cai no wizard por token velho). Prioriza correção sobre a economia marginal de uma leitura.

**Anexos passam pelo ECS, não presigned-direto-do-cliente.** Upload vai para o ECS, que grava no S3 e publica um evento na fila SQS. Alternativa descartada: presigned URL (cliente sobe direto no S3) — mais barato/rápido, mas tira o controle do backend sobre o que entra; como o objetivo é manter esse controle (mesmo sem validar conteúdo ainda), o fluxo passa pelo ECS.

**Fila SQS para anexos desde o MVP, mesmo sem validação de conteúdo.** Não há trabalho pesado a tirar da request hoje (PDFs simples, sem scan) — a fila é upgrade-path deliberado: quando entrar validação/antivírus, não precisa redesenhar o fluxo de upload, só plugar um worker na fila já existente. Documentado como troca consciente (menos simples agora, evita retrabalho depois).

**Auth: Cognito Authorizer no API Gateway + revalidação no ECS.** API Gateway valida o JWT via Cognito Authorizer antes de rotear; o ECS revalida a assinatura via Spring Security resource server (não confia cegamente no gateway — defesa em profundidade). Cognito também é o serviço emulado pelo ministack, mantendo paridade local.

**DynamoDB single-table.** Uma tabela, chaves compostas:

| PK | SK | Uso |
|---|---|---|
| `CLIENTE#{id}` | `PROFILE` | dados pessoais + endereço + renda (inclui estado de draft) |
| `CLIENTE#{id}` | `SOLICITACAO#{id}` | uma solicitação |
| `CLIENTE#{id}` | `ANEXO#{id}` | metadata do anexo (S3 key, status da fila) |

Sem GSI de operador nesta fase (só o próprio cliente consulta as suas). Alternativa descartada: multi-table — mais tabelas para gerenciar sem ganho, já que os acessos são sempre por `CLIENTE#{id}`.

**Frontend estático em S3 + CloudFront.** CloudFront na frente do bucket para TLS próprio, cache e invalidação no deploy — bucket puro sem CDN não cobre isso.

**API Gateway → ECS via NLB + VPC Link.** API Gateway não fala direto com ECS Fargate; precisa de um Network Load Balancer na frente do serviço, exposto via VPC Link. Terraform provisiona VPC, subnets, NLB, ECS service/task-def, API Gateway HTTP API, Cognito User Pool, tabela DynamoDB, buckets S3, fila SQS e as IAM roles de cada peça.

## Risks / Trade-offs

- **[Risco] Fila sem validação aceita qualquer PDF, incluindo malicioso.** → Mitigação: aceito conscientemente para MVP (PDFs simples); scan/validação entra depois plugado na fila já existente, sem redesenho.
- **[Risco] Ordem de etapas do onboarding só é garantida se o backend também validar (front sozinho não basta).** → Mitigação: validação de ordem no domínio (use case), não só no front.
- **[Risco] ministack pode não emular NLB/VPC Link com fidelidade total (feature nova/menos comum que S3/Dynamo).** → Mitigação: validado cedo via spike (task 1.9) — sem confirmação de suporte no ministack (ver infra/README.md). Como este projeto não faz deploy real na AWS, esse gap fica sem validação prática; documentado como limitação conhecida do módulo Terraform de rede.
- **[Risco] Modelagem single-table errada é cara de corrigir depois.** → Mitigação: fechar os padrões de acesso (listados acima) antes de implementar os repositórios hexagonais.

## Migration Plan

Projeto novo, sem dados existentes para migrar. Ordem de implementação sugerida (cada uma como possível branch `feature/*` própria):
1. Infra base via Terraform (VPC, Cognito User Pool, tabela DynamoDB, buckets S3, fila SQS) + docker-compose com ministack.
2. Backend esqueleto hexagonal + `client-auth` (login, validação de token ponta a ponta).
3. `client-profile` (wizard sequencial + gate de perfil completo).
4. `attachments` (upload via ECS → S3 → SQS).
5. `service-requests` (criar solicitação reaproveitando perfil, listar/acompanhar).
6. Frontend React integrado a cada capability conforme fica pronta.

Deploy real na AWS está fora de escopo deste projeto — a infra Terraform é AWS-alvo e fica pronta para aplicação, mas não é aplicada/executada aqui. Validação end-to-end fica restrita ao ambiente local via ministack (com o gap de NLB/VPC Link documentado nos Risks acima).

## Open Questions

- Suporte do ministack a NLB/VPC Link — validar antes de fechar o módulo Terraform de rede local.
- Tamanho máximo e quantidade de anexos por etapa — não definido ainda, decidir em `client-profile`/`attachments`.
