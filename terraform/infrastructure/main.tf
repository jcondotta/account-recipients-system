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