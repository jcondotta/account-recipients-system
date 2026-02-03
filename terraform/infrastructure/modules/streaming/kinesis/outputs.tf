output "recipients_created_stream_name" {
  description = "The name of the recipients.created Kinesis stream"
  value       = aws_kinesis_stream.recipients_created_stream.name
}

output "recipients_created_stream_arn" {
  description = "The ARN of the recipients.created Kinesis stream"
  value       = aws_kinesis_stream.recipients_created_stream.arn
}

output "recipients_deleted_stream_name" {
  description = "The name of the recipients.deleted Kinesis stream"
  value       = aws_kinesis_stream.recipients_deleted_stream.name
}

output "recipients_deleted_stream_arn" {
  description = "The ARN of the recipients.deleted Kinesis stream"
  value       = aws_kinesis_stream.recipients_deleted_stream.arn
}
