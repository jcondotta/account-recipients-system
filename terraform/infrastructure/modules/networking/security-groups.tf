locals {
#   ecr_vpc_endpoint_sg_name             = "ecr-vpc-endpoint-sg-${var.environment}"
#   sqs_vpc_endpoint_sg_name             = "sqs-vpc-endpoint-sg-${var.environment}"
  cloudwatch_logs_vpc_endpoint_sg_name = "recipients-cloudwatch-logs-vpc-endpoint-sg-${var.environment}"
}
#
# resource "aws_security_group" "ecr_vpc_endpoint_sg" {
#   name        = local.ecr_vpc_endpoint_sg_name
#   description = "Security group for ECR VPC endpoint allowing HTTPS traffic from within the VPC"
#   vpc_id      = aws_vpc.this.id
#
#   ingress {
#     description = "Allow inbound HTTPS (443) traffic from within the VPC"
#     from_port   = 443
#     to_port     = 443
#     protocol    = "tcp"
#     cidr_blocks = [aws_vpc.this.cidr_block]
#   }
#
#   egress {
#     description = "Allow all outbound traffic"
#     from_port   = 0
#     to_port     = 0
#     protocol    = "-1"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
#
#   tags = {
#     Name = local.ecr_vpc_endpoint_sg_name
#   }
# }
#
# resource "aws_security_group" "sqs_vpc_endpoint_sg" {
#   name        = local.sqs_vpc_endpoint_sg_name
#   description = "Security group for SQS VPC endpoint allowing HTTPS traffic from within the VPC"
#   vpc_id      = aws_vpc.this.id
#
#   ingress {
#     description = "Allow inbound HTTPS (443) traffic from within the VPC"
#     from_port   = 443
#     to_port     = 443
#     protocol    = "tcp"
#     cidr_blocks = [aws_vpc.this.cidr_block]
#   }
#
#   egress {
#     description = "Allow all outbound traffic"
#     from_port   = 0
#     to_port     = 0
#     protocol    = "-1"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
#
#   tags = {
#     Name = local.sqs_vpc_endpoint_sg_name
#   }
# }
#
resource "aws_security_group" "cloudwatch_logs_vpc_endpoint_sg" {
  name        = local.cloudwatch_logs_vpc_endpoint_sg_name
  description = "Security group for CloudWatch Logs VPC endpoint allowing HTTPS traffic from within the VPC"
  vpc_id      = aws_vpc.this.id

  ingress {
    description = "Allow inbound HTTPS (443) traffic from within the VPC"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.this.cidr_block]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = local.cloudwatch_logs_vpc_endpoint_sg_name
    Tier = local.tiers.observability
  }
}