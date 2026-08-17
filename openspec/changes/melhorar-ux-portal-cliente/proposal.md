## Why

O portal aceita hoje qualquer valor não-vazio em CPF, CEP, telefone e email — sem checar formato
ou dígito verificador — o que deixa dado inválido entrar no perfil do cliente sem aviso. Além
disso, três fricções de uso já identificadas prejudicam a experiência: a tela de detalhe da
solicitação não tem caminho de volta pra listagem, o cliente não consegue conferir os anexos que
já enviou, e clicar em "nova solicitação" sempre cria uma solicitação nova mesmo quando o cliente
já tem uma em aberto — obrigando a preencher tudo de novo em vez de continuar editando a que já
existe.

## What Changes

- Validação de CPF (formato + dígito verificador), CEP (formato `00000-000`) e telefone (DDD +
  8/9 dígitos) no backend (`PerfilController`) via validators customizados Jakarta, reaproveitados
  no PATCH de solicitação abaixo. Front (`DadosPessoaisForm.tsx`, `EnderecoForm.tsx`) ganha máscara
  + validação antes do submit.
- Validação de formato de email no front (`SignupPage.tsx`) antes do submit — backend já valida
  via `@field:Email` em `AuthController`; falta refletir isso na spec e no client-side.
- `SolicitacaoDetailPage.tsx` ganha link/botão de volta pra listagem (`/`).
- Cliente passa a poder visualizar (não baixar) os próprios anexos: novo `GET` de listagem no
  nível de perfil + visualização inline (embed), sem endpoint nem botão de download.
- Regra de "uma solicitação ativa por vez": dashboard detecta se o cliente já tem solicitação
  `ABERTA` (via `GET /api/solicitacoes` existente); se sim, botão vira "Continuar solicitação" e
  abre o detalhe em modo edição em vez de criar uma nova. Novo `PATCH /api/solicitacoes/{id}`
  (permitido só com status `ABERTA`, valida ownership via `clienteId` do JWT) edita os campos
  snapshot da própria solicitação (`dadosPessoais`/`endereco`/`renda`) — sem repuxar do `Perfil`,
  mantendo a semântica de snapshot já documentada em `Solicitacao.kt`.

## Capabilities

### New Capabilities

_Nenhuma — tudo se encaixa em capabilities já existentes._

### Modified Capabilities

- `client-profile`: CPF, CEP e telefone passam a exigir formato válido (CPF com dígito
  verificador), não só presença.
- `client-auth`: cadastro passa a exigir email em formato válido também no client-side (backend já
  valida; passa a ser requisito explícito da spec).
- `service-requests`: cliente só pode ter uma solicitação `ABERTA` por vez; solicitação `ABERTA` é
  editável via novo endpoint `PATCH`; criar "nova" quando já existe uma `ABERTA` retorna/direciona
  pra ela em vez de criar outra.
- `attachments`: cliente pode listar e visualizar (sem baixar) os próprios anexos enviados.

## Impact

- **Backend**: `PerfilController`/`DadosPessoaisRequest`/`EnderecoRequest` (novos validators),
  novo `SolicitacaoController#atualizar` + `port/input/AtualizarSolicitacaoUseCase` +
  `SolicitacaoService`, novo `AnexoController#listar` + `port/input` correspondente +
  `AnexoRepository#listarPorCliente`, `SolicitacaoRepository` ganha consulta por
  `clienteId`+status `ABERTA`.
- **Frontend**: `DadosPessoaisForm.tsx`, `EnderecoForm.tsx`, `SignupPage.tsx` (máscaras +
  validação), `SolicitacaoDetailPage.tsx` (botão voltar + modo edição), `DashboardPage.tsx`
  (lógica "continuar vs nova"), nova tela/seção de anexos.
- **Specs**: `openspec/specs/client-profile/spec.md`, `client-auth/spec.md`,
  `service-requests/spec.md`, `attachments/spec.md`.
- Sem mudança de infra (DynamoDB single-table, S3, SQS já suportam o necessário).
