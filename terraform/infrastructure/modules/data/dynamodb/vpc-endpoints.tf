resource "aws_vpc_endpoint" "dynamodb_gateway_vpc_endpoint" {
  vpc_id            = var.vpc_id
  service_name      = "com.amazonaws.${var.aws_region}.dynamodb"
  vpc_endpoint_type = "Gateway"

  route_table_ids = var.route_table_ids

  tags = {
    Name = "recipients-dynamodb-vpc-endpoint-${var.environment}"
    Tier = local.tier
  }
}