resource "aws_route_table" "private_route_table" {
  vpc_id = aws_vpc.this.id

  tags = {
    Name = "recipients-private-rt",
    Tier = local.tiers.network
  }
}

resource "aws_route_table_association" "route_table_private_subnet_associations" {
  for_each = aws_subnet.private_subnets

  subnet_id      = each.value.id
  route_table_id = aws_route_table.private_route_table.id
}
