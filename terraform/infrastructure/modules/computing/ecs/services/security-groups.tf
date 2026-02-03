resource "aws_security_group" "ecs_recipients_task_sg" {
  name_prefix = "ecs-recipients-task-sg-"
  description = "Security group for ECS Fargate recipients service tasks"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Allow traffic from ALB to recipients service"
    from_port       = local.ecs_recipients_service_container_port
    to_port         = local.ecs_recipients_service_container_port
    protocol        = "tcp"
    security_groups = [var.lb_security_group_id]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "ecs-recipients-task-sg-${var.environment}"
    Tier = local.tier
  }
}
