resource "aws_vpc_endpoint" "cloudwatch_logs_vpc_endpoint" {
  vpc_id             = aws_vpc.this.id
  service_name       = "com.amazonaws.${var.aws_region}.logs"
  vpc_endpoint_type  = "Interface"
  subnet_ids         = values(aws_subnet.private_subnets)[*].id
  private_dns_enabled = true

  security_group_ids = [
    aws_security_group.cloudwatch_logs_vpc_endpoint_sg.id
  ]

  tags = {
    Name = "recipients-cloudwatch-logs-vpc-endpoint-${var.environment}"
    Tier = local.tiers.observability
  }
}
#
# resource "aws_vpc_endpoint" "ecr_api_vpc_endpoint" {
#   vpc_id            = aws_vpc.this.id
#   service_name      = "com.amazonaws.${var.aws_region}.ecr.api"
#   vpc_endpoint_type = "Interface"
#   subnet_ids        = values(aws_subnet.private_subnets)[*].id
#   private_dns_enabled = true
#
#   security_group_ids = [
#     aws_security_group.ecr_vpc_endpoint_sg.id
#   ]
#
#   tags = {
#     Name = "ecr-vpc-endpoint-${var.environment}"
#   }
# }
#
# resource "aws_vpc_endpoint" "ecr_dkr_vpc_endpoint" {
#   vpc_id            = aws_vpc.this.id
#   service_name      = "com.amazonaws.${var.aws_region}.ecr.dkr"
#   vpc_endpoint_type = "Interface"
#   subnet_ids        = values(aws_subnet.private_subnets)[*].id
#   private_dns_enabled = true
#
#   security_group_ids = [
#     aws_security_group.ecr_vpc_endpoint_sg.id
#   ]
#
#   tags = {
#     Name = "ecr-dkr-vpc-endpoint-${var.environment}"
#   }
# }
#