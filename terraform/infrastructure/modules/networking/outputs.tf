output "vpc_id" {
  description = "The ID of the cards VPC"
  value       = aws_vpc.this.id
}

output "vpc_cidr_block" {
  description = "CIDR block of the VPC"
  value       = aws_vpc.this.cidr_block
}

output "private_subnet_ids" {
  description = "The IDs of the private subnets"
  value       = [for subnet in aws_subnet.private_subnets : subnet.id]
}

output "public_subnet_ids" {
  description = "The IDs of the public subnets"
  value       = [for subnet in aws_subnet.public_subnets : subnet.id]
}

output "private_route_table_id" {
  description = "Private route table ID"
  value       = aws_route_table.private_route_table.id
}
