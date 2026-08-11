locals {
  name = "${var.project_name}-${var.environment}"
}

resource "aws_sqs_queue" "attachments_dlq" {
  name                      = "${local.name}-attachments-dlq"
  sqs_managed_sse_enabled   = true
}

resource "aws_sqs_queue" "attachments" {
  name                       = "${local.name}-attachments"
  visibility_timeout_seconds = 60
  sqs_managed_sse_enabled    = true

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.attachments_dlq.arn
    maxReceiveCount     = 5
  })
}
