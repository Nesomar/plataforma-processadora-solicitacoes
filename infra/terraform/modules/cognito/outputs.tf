output "user_pool_id" {
  value = aws_cognito_user_pool.clients.id
}

output "user_pool_arn" {
  value = aws_cognito_user_pool.clients.arn
}

output "user_pool_client_id" {
  value = aws_cognito_user_pool_client.web.id
}

output "issuer_url" {
  description = "URL do issuer usada pra validar a claim iss do JWT. O Cognito (real ou ministack) sempre carimba os tokens com esse formato — não muda em dev local, só o endpoint de onde as chaves JWKS são buscadas muda (ver jwk_set_uri)."
  value = "https://cognito-idp.${data.aws_region.current.name}.amazonaws.com/${aws_cognito_user_pool.clients.id}"
}

output "jwk_set_uri" {
  description = "URL pra buscar as chaves JWKS. Só difere do padrão AWS quando local_dev_endpoint é setado (ministack não é alcançável no DNS real da AWS a partir de dentro do container)."
  value = var.local_dev_endpoint != "" ? "${var.local_dev_endpoint}/${aws_cognito_user_pool.clients.id}/.well-known/jwks.json" : ""
}
