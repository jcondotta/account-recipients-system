resource "aws_ecs_service" "ecs_recipients_service" {
  name            = local.ecs_recipients_service_name
  cluster         = var.ecs_cluster_id
  task_definition = aws_ecs_task_definition.ecs_recipients_service_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200

  enable_execute_command = true

  network_configuration {
    subnets = var.service_subnet_ids
    security_groups = [
      aws_security_group.ecs_recipients_task_sg.id
    ]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.lb_target_group_arn
    container_name   = local.ecs_recipients_service_container_name
    container_port   = local.ecs_recipients_service_container_port
  }

  tags = {
    Name = local.ecs_recipients_service_name
    Tier = local.tier
  }
}