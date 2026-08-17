## 1. Validators de formato (CPF/CEP/telefone) — backend

- [x] 1.1 Criar `ValidCpf`/`CpfValidator` (constraint jakarta, dígito verificador) em `adapter/input/web` (ou pacote `validation` dedicado)
- [x] 1.2 Criar `ValidCep`/`CepValidator` (8 dígitos)
- [x] 1.3 Criar `ValidTelefone`/`TelefoneValidator` (DDD + 8/9 dígitos)
- [x] 1.4 Aplicar as 3 constraints em `DadosPessoaisRequest`/`EnderecoRequest` (`PerfilController.kt`)
- [x] 1.5 Testes unitários dos 3 validators (casos válidos, inválidos, edge cases de CPF conhecidos como sequência repetida)
- [x] 1.6 Teste de integração `PerfilController` rejeitando CPF/CEP/telefone malformado com 400
- [ ] 1.7 Rodar `./gradlew test --no-daemon --console=plain` (skill `gradle-safe-test`), code review, commit

## 2. Solicitação: uma ativa por vez + edição

- [x] 2.1 `SolicitacaoRepository` (port/output): adicionar `buscarAbertaPorCliente(clienteId): Solicitacao?`
- [x] 2.2 Implementar em `SolicitacaoDynamoDbRepository` (query por `PK=CLIENTE#{id}`, filtro `SK` prefix + status)
- [x] 2.3 `SolicitacaoService.criar()`: consultar `buscarAbertaPorCliente` antes de criar; retornar existente se houver
- [x] 2.4 `SolicitacaoService.atualizar(id, clienteId, dados)`: valida `status == ABERTA` e ownership, atualiza `dadosPessoais`/`endereco`/`renda` (adicionado como 4º método em `SolicitacaoUseCase` — não interface nova — pra seguir o padrão já usado no resto do projeto, um port por capability)
- [x] 2.5 `SolicitacaoController`: novo `PATCH /api/solicitacoes/{id}` com o request reaproveitando `@ValidCpf`/`@ValidCep`/`@ValidTelefone` da seção 1
- [x] 2.6 Erros: 409 (status != ABERTA); 403/404 colapsados em 404 — isolamento por clienteId já vem da chave DynamoDB (`buscarPorId` escopado), mesmo padrão do `buscar()` existente; não vaza se o id existe pra outro dono (CLAUDE.md: "isolamento vem da chave, não de checagem de ownership em código")
- [x] 2.7 Testes: criação idempotente, PATCH feliz, PATCH em solicitação de outro cliente (404), PATCH em id inexistente (404). PATCH em não-ABERTA não tem teste — `SolicitacaoStatus` só tem o valor `ABERTA` hoje, cenário é estruturalmente inalcançável (guard fica como defesa futura)
- [x] 2.8 Rodar testes, code review, commit

## 3. Anexos: listagem e visualização inline

- [x] 3.1 `AnexoRepository` (port/output): adicionar `listarPorCliente(clienteId): List<Anexo>` e `buscar(id, clienteId): Anexo?`
- [x] 3.2 Implementar em `AnexoDynamoDbRepository`
- [x] 3.3 `AnexoService.listarAnexos`/`visualizarAnexo` implementados em `AnexoUseCase` existente (mesmo padrão do item 2.4 — porta única por capability); `ArquivoStorage` ganhou `ler(key)` pro S3
- [x] 3.4 `AnexoController`: novo `GET /api/perfil/anexos` (lista, metadata) e `GET /api/perfil/anexos/{id}` (stream do arquivo, `Content-Disposition: inline`)
- [x] 3.5 Checagem de ownership (`clienteId` do JWT) nos dois endpoints novos
- [x] 3.6 Testes: listagem só retorna anexos do próprio cliente, acesso a anexo de outro cliente nega, header `Content-Disposition: inline` presente
- [x] 3.7 Rodar testes, code review, commit

## 4. Frontend: validação de campos

- [x] 4.1 Função util de validação/máscara de CPF (com dígito verificador) reaproveitada em `DadosPessoaisForm.tsx`
- [x] 4.2 Função util de máscara/validação de CEP e telefone em `DadosPessoaisForm.tsx`/`EnderecoForm.tsx`
- [x] 4.3 Validação de formato de email antes do submit em `SignupPage.tsx`
- [x] 4.4 Mensagens de erro inline nos 3 forms (sem submeter até corrigir)
- [x] 4.5 `npm test` (vitest) cobrindo os validators novos, `npm run build`, code review, commit

## 5. Frontend: navegação e fluxo de solicitação

- [x] 5.1 Botão/link de volta pra listagem em `SolicitacaoDetailPage.tsx`
- [x] 5.2 `DashboardPage.tsx`: checar se já existe solicitação `ABERTA` (via `GET /api/solicitacoes`) e trocar rótulo/ação do botão ("Continuar solicitação" → navega pro detalhe existente)
- [x] 5.3 `SolicitacaoDetailPage.tsx`: modo edição (form com os mesmos validators da seção 4) quando status `ABERTA`, chamando o novo `PATCH` (precisou estender `SolicitacaoResponse` do backend com o snapshot completo — `dadosPessoais`/`endereco`/`renda` — pra pré-preencher o form)
- [x] 5.4 `solicitacoesApi.ts`: adicionar método `atualizar(id, dados)`
- [x] 5.5 `npm test`, `npm run build`, code review, commit

## 6. Frontend: visualização de anexos

- [x] 6.1 `anexosApi.ts`: adicionar `listar()` e `visualizar(id)` (busca como blob autenticado — endpoint exige Bearer token, `<embed src>` não manda header custom)
- [x] 6.2 Nova seção/tela "Meus anexos" (`AnexosPage.tsx`, rota `/anexos`) listando anexos do cliente, link a partir do Dashboard
- [x] 6.3 Visualização inline (embed de PDF via object URL do blob), sem link/botão de download
- [x] 6.4 `npm test`, `npm run build`, code review, commit

## 7. Verificação final

- [x] 7.1 E2E manual via docker-compose + ministack: cadastro com email inválido rejeitado (400), onboarding com CPF/CEP/telefone inválido rejeitado (400), criar solicitação → clicar "nova" de novo continua a mesma (idempotente, mesmo id), editar solicitação aberta (PATCH + validação inline no front), visualizar anexo sem opção de download (Content-Disposition: inline, embed no front), botão voltar no detalhe, isolamento entre clientes (404 em anexo/solicitação de outro cliente). Achado no code review corrigido durante o E2E: `Endereco.complemento` pode vir `null` do backend — `SolicitacaoDetailPage.tsx` normaliza pra `""` ao carregar o form de edição
- [ ] 7.2 Sync das specs (`openspec-sync-specs`) e archive do change
