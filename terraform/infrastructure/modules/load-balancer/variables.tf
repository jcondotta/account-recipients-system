variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "subnet_ids" {
  description = "The IDs of the subnets where the load balancer will be deployed."
  type = list(string)
}