output "security_group_id" {
  description = "The ID of the security group associated with the load balancer."
  value       = aws_security_group.load_balancer_sg.id
}