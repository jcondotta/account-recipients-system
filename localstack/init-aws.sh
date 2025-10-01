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

echo "✅ DynamoDB table 'account-recipients' with LSI created successfully."
