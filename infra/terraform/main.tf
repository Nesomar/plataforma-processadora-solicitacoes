module "network" {
  source = "./modules/network"

  project_name       = var.project_name
  environment        = var.environment
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
}

module "cognito" {
  source = "./modules/cognito"

  project_name = var.project_name
  environment  = var.environment
}

module "dynamodb" {
  source = "./modules/dynamodb"

  project_name = var.project_name
  environment  = var.environment
}

module "s3" {
  source = "./modules/s3"

  project_name = var.project_name
  environment  = var.environment
}

module "sqs" {
  source = "./modules/sqs"

  project_name = var.project_name
  environment  = var.environment
}

module "api_gateway" {
  source = "./modules/api_gateway"

  project_name       = var.project_name
  environment        = var.environment
  vpc_id             = module.network.vpc_id
  private_subnet_ids = module.network.private_subnet_ids
  nlb_listener_arn   = module.network.nlb_listener_arn
}

module "ecs" {
  source = "./modules/ecs"

  project_name            = var.project_name
  environment             = var.environment
  vpc_id                  = module.network.vpc_id
  vpc_cidr                = var.vpc_cidr
  private_subnet_ids      = module.network.private_subnet_ids
  target_group_arn        = module.network.ecs_target_group_arn
  dynamodb_table_arn      = module.dynamodb.table_arn
  attachments_bucket_arn  = module.s3.attachments_bucket_arn
  sqs_queue_arn           = module.sqs.queue_arn
}
