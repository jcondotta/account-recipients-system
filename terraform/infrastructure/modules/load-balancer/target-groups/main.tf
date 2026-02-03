locals {
  lb_tg_recipients = "lb-tg-recipients"
}

resource "aws_lb_target_group" "this" {
  name        = local.lb_tg_recipients
  port        = var.lb_target_group_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    interval            = 15
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
    matcher             = "200"
  }

  tags = {
    Name = local.lb_tg_recipients
    Tier = "network"
  }
}
