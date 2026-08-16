output "api_endpoint" {
  value = module.api_gateway.api_endpoint
}

output "dynamodb_table_name" {
  value = module.dynamodb.table_name
}

output "frontend_bucket_name" {
  value = module.s3.frontend_bucket_name
}

output "cloudfront_distribution_id" {
  value = module.s3.cloudfront_distribution_id
}

output "cloudfront_domain_name" {
  value = module.s3.cloudfront_domain_name
}

output "attachments_bucket_name" {
  value = module.s3.attachments_bucket_name
}

output "attachments_queue_url" {
  value = module.sqs.queue_url
}

output "ecs_cluster_name" {
  value = module.ecs.cluster_name
}

output "ecs_service_name" {
  value = module.ecs.service_name
}
