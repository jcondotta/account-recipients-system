resource "aws_kinesis_stream" "recipients_created_stream" {
  name        = local.recipients_created_stream_name
  shard_count = 1

  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }

  encryption_type = "KMS"
  kms_key_id      = "alias/aws/kinesis"

  tags = {
    Name = local.recipients_created_stream_name
    Tier = local.tier
  }
}

resource "aws_kinesis_stream" "recipients_deleted_stream" {
  name        = local.recipients_deleted_stream_name
  shard_count = 1

  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }

  encryption_type = "KMS"
  kms_key_id      = "alias/aws/kinesis"

  tags = {
    Name = local.recipients_deleted_stream_name
    Tier = local.tier
  }
}
