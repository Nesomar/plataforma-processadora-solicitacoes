locals {
  name = "${var.project_name}-${var.environment}"
}

data "aws_region" "current" {}

resource "aws_cognito_user_pool" "clients" {
  name = "${local.name}-clientes"

  username_attributes     = ["email"]
  auto_verified_attributes = ["email"]

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_uppercase = true
    require_numbers   = true
    require_symbols   = false
  }

  tags = { Name = "${local.name}-clientes" }
}

# Client publico (SPA React) sem secret; login direto via USER_PASSWORD_AUTH (sem Hosted UI)
resource "aws_cognito_user_pool_client" "web" {
  name         = "${local.name}-web"
  user_pool_id = aws_cognito_user_pool.clients.id

  generate_secret                     = false
  explicit_auth_flows                 = ["ALLOW_USER_PASSWORD_AUTH", "ALLOW_REFRESH_TOKEN_AUTH"]
  access_token_validity               = 60
  id_token_validity                   = 60
  refresh_token_validity              = 30
  token_validity_units {
    access_token  = "minutes"
    id_token      = "minutes"
    refresh_token = "days"
  }
  prevent_user_existence_errors = "ENABLED"
}
