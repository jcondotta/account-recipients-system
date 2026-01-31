output "recipients_table_name" {
  description = "The name of the DynamoDB recipients table "
  value       = aws_dynamodb_table.recipients.name
}

output "recipients_table_arn" {
  description = "The ARN of the DynamoDB recipients table"
  value       = aws_dynamodb_table.recipients.arn
}