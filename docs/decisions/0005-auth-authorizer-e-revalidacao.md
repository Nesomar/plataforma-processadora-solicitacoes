# 5. Cognito Authorizer no API Gateway + revalidação no backend

**Status:** aceito

## Contexto

JWT do Cognito precisa ser validado antes de qualquer request chegar no ECS.

## Decisão

Duas camadas: API Gateway valida via Cognito JWT Authorizer (rejeita antes de rotear); o backend
(`SecurityConfig`, Spring Security resource server) revalida a assinatura de novo, de forma
independente.

## Raciocínio

Defesa em profundidade — o backend não confia cegamente em "o gateway já validou". Se o gateway
tiver uma authorizer mal configurada (ou for contornado por engano numa rota interna), o backend
ainda barra token inválido.

## Trade-off aceito

Validação duplicada (mesma assinatura conferida duas vezes) por request autenticado. Custo de CPU
desprezível perto do ganho de não ter um único ponto de falha na autenticação.
