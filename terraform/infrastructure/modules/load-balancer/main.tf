resource "aws_lb" "this" {
  name               = "recipients-load-balancer-${var.environment}"
  load_balancer_type = "application"

  subnets = var.subnet_ids

  security_groups = [
    aws_security_group.load_balancer_sg.id
  ]

  tags = {
    Name = var.load_balancer_name,
    tier = "network"
  }
}