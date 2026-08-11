variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "vpc_cidr" {
  type = string
}

variable "private_subnet_ids" {
  type = list(string)
}

variable "target_group_arn" {
  description = "Target group do NLB (modulo network) onde o service registra as tasks"
  type        = string
}

variable "container_port" {
  type    = number
  default = 8080
}

variable "container_image" {
  description = "Imagem placeholder ate o backend real ser publicado (task 6.3 atualiza via deploy)"
  type        = string
  default     = "public.ecr.aws/nginx/nginx:stable"
}

variable "desired_count" {
  type    = number
  default = 1
}

variable "dynamodb_table_arn" {
  type = string
}

variable "attachments_bucket_arn" {
  type = string
}

variable "sqs_queue_arn" {
  type = string
}
