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
