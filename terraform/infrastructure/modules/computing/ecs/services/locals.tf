locals {
  ecs_recipients_service_name               = "ecs-recipients-service"
  ecs_recipients_service_container_name     = "recipients-service-container"
  ecs_recipients_service_container_port     = 8072
  ecs_recipients_service_container_protocol = "tcp"
  tier                                      = "computing"
}
