resource "aws_ecs_cluster" "this" {
  name = local.ecs_recipients_cluster_name

  tags = {
    Name = local.ecs_recipients_cluster_name
    Tier = local.tier
  }
}