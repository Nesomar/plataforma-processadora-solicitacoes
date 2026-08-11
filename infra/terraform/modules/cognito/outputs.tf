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
  description = "URL do issuer usada para validar o JWT (Spring Security resource server) e no Cognito Authorizer do API Gateway"
  value = var.local_dev_endpoint != "" ? "${var.local_dev_endpoint}/${aws_cognito_user_pool.clients.id}" : "https://cognito-idp.${data.aws_region.current.name}.amazonaws.com/${aws_cognito_user_pool.clients.id}"
}
