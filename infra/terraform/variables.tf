variable "aws_region" {
  description = "Regiao AWS onde a infra e provisionada"
  type        = string
  default     = "sa-east-1"
}

variable "environment" {
  description = "Nome do ambiente (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Prefixo usado no nome dos recursos"
  type        = string
  default     = "portal-cliente"
}

variable "vpc_cidr" {
  description = "CIDR block da VPC"
  type        = string
  default     = "10.20.0.0/16"
}

variable "availability_zones" {
  description = "AZs usadas para as subnets publicas/privadas"
  type        = list(string)
  default     = ["sa-east-1a", "sa-east-1b"]
}

variable "container_port" {
  description = "Porta que o backend escuta; usada tanto no target group do NLB (network) quanto no task-def (ecs)"
  type        = number
  default     = 8080
}

variable "local_dev_origins" {
  description = "Origens de dev local autorizadas via CORS na API (ex: Vite dev server), além do domínio real do CloudFront. Vazio por padrão para não vazar pra prod/staging — setar explicitamente ao aplicar localmente (ex: -var 'local_dev_origins=[\"http://localhost:5173\"]')."
  type        = list(string)
  default     = []
}

variable "local_dev_endpoint" {
  description = "Endpoint do ministack (ex: http://ministack:4566) usado para apontar o issuer_url do Cognito pro emulador local em vez do DNS real da AWS. Vazio por padrão para não vazar pra prod/staging."
  type        = string
  default     = ""
}
