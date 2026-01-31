resource "aws_vpc_endpoint" "kinesis_interface_vpc_endpoint" {
  vpc_id              = var.vpc_id
  service_name        = "com.amazonaws.${var.aws_region}.kinesis-streams"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.subnet_ids
  private_dns_enabled = true

  security_group_ids = [
    aws_security_group.kinesis_vpc_endpoint_sg.id
  ]

  tags = {
    Name = "recipients-kinesis-vpc-endpoint-${var.environment}"
    Tier = local.tier
  }
}
