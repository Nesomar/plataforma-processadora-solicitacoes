# 1. DynamoDB single-table

**Status:** aceito

## Contexto

Três tipos de dado por cliente (perfil, solicitações, anexos), sempre acessados por `clienteId`.
Nenhum acesso hoje precisa de operador/backoffice consultando por outro eixo.

## Decisão

Uma tabela só, chave composta `PK=CLIENTE#{id}` / `SK` variando por tipo (`PROFILE`,
`SOLICITACAO#{id}`, `ANEXO#{id}`). Sem GSI.

## Alternativa descartada

Multi-table (uma tabela por entidade). Mais tabelas pra gerenciar sem ganho real, já que todo
acesso é sempre por `CLIENTE#{id}` — o padrão de acesso não muda entre entidades.

## Trade-off aceito

Modelagem errada aqui é cara de corrigir depois (migração de single-table é mais trabalhosa que
adicionar uma coluna numa tabela relacional). Mitigado fechando os padrões de acesso antes de
implementar os repositórios (ver `docs/architecture.md`).
