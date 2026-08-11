variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "local_dev_endpoint" {
  description = "Endpoint do ministack (ex: http://ministack:4566); quando setado, issuer_url aponta pra ele em vez do DNS real da AWS."
  type        = string
  default     = ""
}
