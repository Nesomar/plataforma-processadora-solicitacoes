output "vpc_id" {
  value = aws_vpc.this.id
}

output "public_subnet_ids" {
  value = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  value = aws_subnet.private[*].id
}

output "nlb_arn" {
  value = aws_lb.internal.arn
}

output "nlb_listener_arn" {
  value = aws_lb_listener.ecs.arn
}

output "nlb_dns_name" {
  value = aws_lb.internal.dns_name
}

output "ecs_target_group_arn" {
  value = aws_lb_target_group.ecs.arn
}
