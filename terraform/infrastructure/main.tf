module "networking" {
  source = "./modules/networking"

  aws_region  = var.aws_region
  environment = var.environment
}

module "dynamodb" {
  source = "./modules/data/dynamodb"

  aws_region  = var.aws_region
  environment = var.environment

  vpc_id = module.networking.vpc_id

  route_table_ids = [
    module.networking.private_route_table_id
  ]

  depends_on = [
    module.networking
  ]
}

module "kinesis" {
  source = "./modules/streaming/kinesis"

  aws_region  = var.aws_region
  environment = var.environment

  vpc_id = module.networking.vpc_id

  subnet_ids = module.networking.private_subnet_ids
  vpc_cidr_blocks = [
    module.networking.vpc_cidr_block
  ]

  depends_on = [
    module.networking
  ]
}

module "ecs_recipients_cluster" {
  source = "./modules/computing/ecs/cluster"

  environment = var.environment
}

module "ecs_recipients_service" {
  source = "./modules/computing/ecs/services"

  aws_region  = var.aws_region
  environment = var.environment

  vpc_id             = module.networking.vpc_id
  service_subnet_ids = module.networking.private_subnet_ids

  ecs_cluster_id = module.ecs_recipients_cluster.ecs_cluster_id
  # container_image = data.terraform_remote_state.bootstrap.outputs.management_service_ecr_url

  lb_security_group_id = module.load_balancer.security_group_id
  lb_target_group_arn  = module.lb_target_group_recipients_service.target_group_arn

  dynamodb_recipients_table_arn = module.dynamodb.recipients_table_arn
  kinesis_recipients_created_stream_arn = module.kinesis.recipients_created_stream_arn
  kinesis_recipients_deleted_stream_arn = module.kinesis.recipients_deleted_stream_arn

  environment_variables = {
    "AWS_DEFAULT_REGION"                         = var.aws_region
    "AWS_DYNAMODB_ACCOUNT_RECIPIENTS_TABLE_NAME" = module.dynamodb.recipients_table_name,
    "AWS_KINESIS_RECIPIENTS_CREATED_STREAM_NAME" = module.kinesis.recipients_created_stream_name,
    "AWS_KINESIS_RECIPIENTS_DELETED_STREAM_NAME" = module.kinesis.recipients_deleted_stream_name,
  }
}

module "load_balancer" {
  source = "./modules/load-balancer"

  environment = var.environment

  vpc_id     = module.networking.vpc_id
  subnet_ids = module.networking.public_subnet_ids
}

module "lb_target_group_recipients_service" {
  source = "./modules/load-balancer/target-groups"

  vpc_id = module.networking.vpc_id

  lb_target_group_port = module.ecs_recipients_service.ecs_management_service_port
}
