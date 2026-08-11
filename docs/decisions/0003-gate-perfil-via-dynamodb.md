# 3. Gate "perfil completo" via consulta ao DynamoDB, não claim no JWT

**Status:** aceito

## Contexto

A cada login (e em pontos decisivos, como criar solicitação), o sistema precisa saber se o
cliente já completou o onboarding — pra mandar ele pro wizard ou direto pro dashboard.

## Decisão

`GetItem` no DynamoDB (`PerfilService.consultarGate`) confere se `dadosPessoais`/`endereco`/`renda`
estão todos preenchidos.

## Alternativa descartada

Custom claim no JWT (`perfilCompleto: true`), atualizado quando o cadastro termina. Mais barato
em teoria (sem leitura extra), mas fica desatualizado até o token dar refresh — cliente completa o
cadastro na última etapa e ainda cairia no wizard de novo por causa do token velho (viola o
cenário "perfil completado durante a sessão reflete imediatamente", `specs/client-profile`).

## Trade-off aceito

Uma leitura a mais por decisão de roteamento. Prioriza correção sobre a economia marginal — `GetItem`
por chave é barato o bastante pra não importar.
