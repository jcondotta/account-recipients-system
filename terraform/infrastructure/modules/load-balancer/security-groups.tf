locals {
  load_balancer_sg_name = "recipients-load-balancer-sg-${var.environment}"
}

resource "aws_security_group" "load_balancer_sg" {
  name        = local.load_balancer_sg_name
  description = "Security group for the recipients Application Load Balancer (ALB), allowing inbound HTTP traffic."

  vpc_id = var.vpc_id

  ingress {
    description = "Allow inbound HTTP traffic (port 80) from all sources"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound traffic from the ALB"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = local.load_balancer_sg_name
    Tier = "network"
  }
}