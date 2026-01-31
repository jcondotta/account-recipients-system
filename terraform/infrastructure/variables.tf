variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., qa, staging, prod)"
  type        = string
}

variable "aws_profile" {
  description = "The AWS profile to use for deployment."
  type        = string
}