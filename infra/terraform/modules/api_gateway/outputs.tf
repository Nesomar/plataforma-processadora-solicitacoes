output "api_id" {
  value = aws_apigatewayv2_api.this.id
}

output "api_endpoint" {
  value = aws_apigatewayv2_stage.default.invoke_url
}

output "route_id" {
  value = aws_apigatewayv2_route.proxy.id
}
