resource "aws_cloudwatch_log_group" "ecs_recipients_service_logs" {
  name              = "/ecs/recipients/service"
  retention_in_days = 1

  tags = {
    Name = "ecs-recipients-service-logs"
    Tier = local.tier
  }
}