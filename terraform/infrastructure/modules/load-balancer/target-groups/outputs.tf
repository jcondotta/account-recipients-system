output "target_group_id" {
  description = "The ID of the ALB target group for the management-service."
  value       = aws_lb_target_group.this.id
}

output "target_group_arn" {
  description = "The ARN of the ALB target group for the management-service."
  value       = aws_lb_target_group.this.arn
}
