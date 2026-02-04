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

variable "route_table_ids" {
  description = "List of private route table IDs used for the DynamoDB Gateway VPC Endpoint"
  type = list(string)
}