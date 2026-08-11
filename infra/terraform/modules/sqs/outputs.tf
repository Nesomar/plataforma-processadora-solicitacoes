output "queue_url" {
  value = aws_sqs_queue.attachments.url
}

output "queue_arn" {
  value = aws_sqs_queue.attachments.arn
}
