variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "ecs_cluster_id" {
  description = "The ID of the ECS cluster"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "service_subnet_ids" {
  type        = list(string)
  description = "List of subnet IDs where the ECS service will run"
}

variable "container_image" {
  description = "The container image URI for the ECS task."
  type        = string
}

variable "lb_target_group_arn" {
  description = "ARN of the ALB target group for ECS service"
  type        = string
}

variable "lb_security_group_id" {
  description = "ID of the ALB security group for ECS service"
  type        = string
}

variable "dynamodb_recipients_table_arn" {
  description = "The ARN of the recipients DynamoDB table"
  type        = string
}

variable "kinesis_recipients_created_stream_arn" {
  description = "The ARN of the recipients.created Kinesis stream"
  type        = string
}

variable "kinesis_recipients_deleted_stream_arn" {
  description = "The ARN of the recipients.deleted Kinesis stream"
  type        = string
}

variable "environment_variables" {
  type    = map(string)
  default = {}
}