provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "portal-cliente-solicitacoes"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}
