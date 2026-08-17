## Context

Portal hexagonal (Kotlin/Spring WebFlux + React/TS + DynamoDB single-table). Hoje:
- `DadosPessoais`/`Endereco` (domain) não validam formato, só presença (`@NotBlank` em
  `PerfilController`).
- `Solicitacao` é um snapshot imutável dos dados de perfil no momento da criação
  (`status: SolicitacaoStatus { ABERTA }` — único valor); não existe `PATCH`.
- `Anexo` é vinculado a `clienteId` (não a uma `Solicitacao`); só existe `POST` de upload, sem
  leitura.
- `SolicitacaoDetailPage.tsx` é display puro, sem navegação de volta.

## Goals / Non-Goals

**Goals:**
- CPF, CEP e telefone validados por formato (CPF com dígito verificador) no backend, com
  validators reaproveitáveis entre `PerfilController` e o novo `PATCH` de solicitação.
- Email validado no front antes do submit (backend já valida via `@field:Email`).
- Cliente com solicitação `ABERTA` reaproveita/edita ela em vez de criar duplicata.
- Cliente visualiza (inline, sem download) os próprios anexos.
- Botão de volta na tela de detalhe da solicitação.

**Non-Goals:**
- Download de anexo (explicitamente fora de escopo, decisão do usuário).
- Múltiplas solicitações simultâneas por cliente (regra fechada: uma `ABERTA` por vez).
- Vincular anexo a uma `Solicitacao` específica — listagem continua no nível de perfil
  (`clienteId`), sem mudar o modelo de dados de `Anexo`.
- Locking otimista em `Solicitacao`/`Perfil` (gotcha conhecida do projeto, fora de escopo aqui).

## Decisions

### Validação de formato: Bean Validation customizado (jakarta), não lib externa
`@ValidCpf`, `@ValidCep`, `@ValidTelefone` como `ConstraintValidator` em
`adapter/input/web` (ou `domain` se preferir puro-Kotlin + wrapper de constraint), aplicados nos
DTOs de request (`DadosPessoaisRequest`, `EnderecoRequest`, e o novo request de `PATCH`
solicitação). Alternativa descartada: lib de validação de CPF de terceiros — desnecessária,
dígito verificador de CPF é ~15 linhas de aritmética modular, e o projeto já usa
`jakarta.validation` (rung 5 da escada: dependência já instalada resolve, não precisa de nova).
Front replica a mesma regra em JS puro (sem lib de máscara nova) nos 3 forms afetados.

### Uma solicitação ativa por vez: verificação na criação, não constraint de unicidade no banco
`SolicitacaoService.criar()` passa a consultar se já existe `ABERTA` pro `clienteId`
(`SolicitacaoRepository` ganha `buscarAbertaPorCliente`, usando a mesma partição
`PK=CLIENTE#{id}` — filtra por `SK` prefix `SOLICITACAO#` + status em memória, já que
single-table não tem índice secundário hoje) e retorna ela em vez de criar. Alternativa
descartada: constraint condicional no `PutItem` do Dynamo — mais complexo, e a checagem de
aplicação já é suficiente dado que não há concorrência real esperada nesse fluxo (cliente único
clicando um botão).

### Edição da solicitação: PATCH dedicado, reaproveitando `DadosPessoais`/`Endereco`/`Renda`
Novo `PATCH /api/solicitacoes/{id}` com o mesmo shape de request do `PerfilController`
(dados pessoais + endereço + renda), validado com os mesmos `@ValidCpf`/`@ValidCep`/
`@ValidTelefone`. Só aceito se `solicitacao.status == ABERTA` e `solicitacao.clienteId ==
sub do JWT` — mesma checagem de ownership já usada em `buscar()`. Mantém a semântica de
snapshot: edita a cópia da própria `Solicitacao`, não volta a ler do `Perfil`.

### Visualização de anexo: GET com Content-Disposition inline, sem endpoint de download
Novo `GET /api/perfil/anexos` (lista) e `GET /api/perfil/anexos/{id}` (bytes do arquivo,
`Content-Disposition: inline`) — sem gerar link de download separado nem presigned URL exposta
como "salvar". O navegador pode oferecer "salvar como" nativamente (fora do controle da
aplicação), mas a UI não expõe botão de download. Backend intermedia a leitura do S3 (mesmo
padrão hoje usado no upload — `AnexoService` já centraliza acesso ao storage).

### Botão voltar: navegação client-side simples
`useNavigate(-1)` ou `Link to="/"` em `SolicitacaoDetailPage.tsx`. Sem mudança de rota, sem novo
componente compartilhado — é 3 linhas.

## Risks / Trade-offs

- [Checagem de "já existe ABERTA" via scan em memória em vez de índice] → aceitável no volume
  atual (poucas solicitações por cliente); revisar se single-table ganhar GSI por status no
  futuro.
- [Regex/checksum de CPF pode divergir de validação de terceiros (ex: Receita) em casos raros de
  CPF gerado mas nunca emitido] → aceito, é a mesma limitação de qualquer validação client-side de
  CPF; não estamos consultando Receita Federal.
- [Reaproveitar `DadosPessoais`/`Endereco` no request de `PATCH` de solicitação duplica o shape do
  request de `PerfilController`] → aceito conscientemente para não acoplar o endpoint de
  solicitação ao de perfil; são bounded contexts diferentes mesmo compartilhando o domain type.

## Migration Plan

Sem migração de dados — `SolicitacaoStatus` continua com um único valor (`ABERTA`), nenhum campo
novo no schema DynamoDB. Deploy é aditivo: novos endpoints, validators novos que só passam a
rejeitar entrada que já deveria ser inválida (nenhum dado existente precisa ser corrigido
retroativamente, já que validação é só na escrita).

## Open Questions

Nenhuma pendente — decisões fechadas com o usuário durante exploração (uma solicitação ativa por
vez; visualização sem download).
