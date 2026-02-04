resource "aws_dynamodb_table" "recipients" {
  name         = local.recipients_table_name
  billing_mode = "PAY_PER_REQUEST"

  hash_key  = "partitionKey"
  range_key = "sortKey"

  attribute {
    name = "partitionKey"
    type = "S"
  }

  attribute {
    name = "sortKey"
    type = "S"
  }

  attribute {
    name = "recipientName"
    type = "S"
  }

  local_secondary_index {
    name            = "RecipientNameLSI"
    range_key       = "recipientName"
    projection_type = "ALL"
  }

  tags = {
    Name = local.recipients_table_name
    Tier = local.tier
  }
}