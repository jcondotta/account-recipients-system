locals {
  recipients_created_stream_name = "recipients.created.${var.environment}"
  recipients_deleted_stream_name = "recipients.deleted.${var.environment}"
  tier                           = "streaming"
}
