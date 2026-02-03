variable "vpc_id" {
  description = "The ID of the VPC where the target group is created."
  type        = string
}

variable "lb_target_group_port" {
  description = "The port for the Load Balancer Target Group"
  type        = number
}