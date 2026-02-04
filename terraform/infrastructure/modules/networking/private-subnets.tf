resource "aws_subnet" "private_subnets" {
  for_each = {
    "us-east-1a" = "10.0.20.0/24"
    "us-east-1b" = "10.0.40.0/24"
    "us-east-1c" = "10.0.60.0/24"
  }

  vpc_id                  = aws_vpc.this.id
  cidr_block              = each.value
  availability_zone       = each.key
  map_public_ip_on_launch = false

  tags = {
    Name = "recipients-private-subnet-${each.key}",
    Tier = local.tiers.network
  }
}