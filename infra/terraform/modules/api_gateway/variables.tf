variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "private_subnet_ids" {
  type = list(string)
}

variable "nlb_listener_arn" {
  description = "ARN do listener do NLB (modulo network) que recebe o trafego do VPC Link"
  type        = string
}

variable "cognito_issuer_url" {
  type = string
}

variable "cognito_user_pool_client_id" {
  type = string
}

variable "allowed_origins" {
  description = "Origens do frontend autorizadas a chamar a API (CORS)"
  type        = list(string)
}
