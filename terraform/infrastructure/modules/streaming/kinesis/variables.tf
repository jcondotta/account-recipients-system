variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "Deployment environment (e.g., dev, prod)"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "subnet_ids" {
  description = "List of private subnet IDs used for the Kinesis Interface VPC Endpoint"
  type        = list(string)
}

variable "vpc_cidr_blocks" {
  description = "List of CIDR blocks allowed to access the Kinesis VPC Interface Endpoint"
  type        = list(string)
}