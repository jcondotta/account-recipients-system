output "ecs_management_service_port" {
  description = "The container port used by the ECS management service"
  value       = var.container_port
}

output "ecs_task_security_group_id" {
  description = "The security group ID for the ECS task running the card management service."
  value       = aws_security_group.ecs_recipients_task_sg.id
}