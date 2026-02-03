resource "aws_iam_role" "ecs_recipients_task_execution_role" {
  name = "ecs-recipients-task-exec-role-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_recipients_task_execution_policy" {
  role       = aws_iam_role.ecs_recipients_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_recipients_task_role" {
  name = "ecs-recipients-task-role-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy" "ecs_recipients_task_policy" {
  name = "ecs-recipients-task-policy-${var.environment}"
  role = aws_iam_role.ecs_recipients_task_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query"
        ]
        Resource = var.dynamodb_recipients_table_arn
      },
      {
        Effect = "Allow"
        Action = [
          "kinesis:PutRecord",
          "kinesis:PutRecords"
        ]
        Resource = [
          var.kinesis_recipients_created_stream_arn,
          var.kinesis_recipients_deleted_stream_arn
        ]
      }
    ]
  })
}
