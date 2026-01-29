#!/bin/bash
set -e

echo "🚀 Creating DynamoDB table: account-recipients..."

awslocal dynamodb create-table \
  --table-name account-recipients \
  --attribute-definitions \
      AttributeName=partitionKey,AttributeType=S \
      AttributeName=sortKey,AttributeType=S \
      AttributeName=recipientName,AttributeType=S \
  --key-schema \
      AttributeName=partitionKey,KeyType=HASH \
      AttributeName=sortKey,KeyType=RANGE \
  --local-secondary-indexes '[
    {
      "IndexName": "RecipientNameLSI",
      "KeySchema": [
        {"AttributeName": "partitionKey", "KeyType": "HASH"},
        {"AttributeName": "recipientName", "KeyType": "RANGE"}
      ],
      "Projection": {
        "ProjectionType": "ALL"
      }
    }
  ]' \
  --billing-mode PAY_PER_REQUEST

echo "✅ DynamoDB table 'account-recipients' created successfully."

echo "🚀 Creating Kinesis stream: recipients.created..."

awslocal kinesis create-stream \
  --stream-name recipients.created \
  --shard-count 1

echo "🚀 Creating Kinesis stream: recipients.deleted..."

awslocal kinesis create-stream \
  --stream-name recipients.deleted \
  --shard-count 1

echo "⏳ Waiting for Kinesis streams to become ACTIVE..."

awslocal kinesis wait stream-exists \
  --stream-name recipients.created

awslocal kinesis wait stream-exists \
  --stream-name recipients.deleted

echo "✅ Kinesis streams 'recipients.created' and 'recipients.deleted' created successfully."