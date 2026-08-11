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
