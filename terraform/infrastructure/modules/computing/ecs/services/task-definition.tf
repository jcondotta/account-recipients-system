resource "aws_ecs_task_definition" "ecs_recipients_service_task" {
  family                   = "ecs-recipients-service-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]

  execution_role_arn = aws_iam_role.ecs_recipients_task_execution_role.arn
  task_role_arn      = aws_iam_role.ecs_recipients_task_role.arn

  cpu    = "512"   # 0.5 vCPU
  memory = "1024"  # 1 GB memory

  container_definitions = jsonencode([
    {
      name      = local.ecs_recipients_service_container_name
      image     = var.container_image
      essential = true

      portMappings = [
        {
          containerPort = local.ecs_recipients_service_container_port
          protocol      = local.ecs_recipients_service_container_protocol
        }
      ]

      environment = [
        for key, value in var.environment_variables : {
          name  = key
          value = value
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.ecs_recipients_service_logs.name
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs/recipients"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:${local.ecs_recipients_service_container_port}/actuator/health || exit 1"]
        interval    = 30
        timeout     = 8
        retries     = 3
        startPeriod = 60
      }
    }
  ])
}
