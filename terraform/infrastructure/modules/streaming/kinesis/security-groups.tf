locals {
  kinesis_vpc_endpoint_sg_name = "recipients-kinesis-vpc-endpoint-sg-${var.environment}"
}

resource "aws_security_group" "kinesis_vpc_endpoint_sg" {
  name        = local.kinesis_vpc_endpoint_sg_name
  description = "Security group for Kinesis VPC Interface Endpoint allowing HTTPS traffic from within the VPC"
  vpc_id      = var.vpc_id

  ingress {
    description = "Allow inbound HTTPS (443) traffic from within the VPC"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = var.vpc_cidr_blocks
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = local.kinesis_vpc_endpoint_sg_name
  }
}