output "pubsub_topic_name" {
  value = module.pubsub.topic_name
}

output "worker_subscription_name" {
  value = module.pubsub.worker_subscription_name
}

output "dead_letter_topic_id" {
  value = module.pubsub.dead_letter_topic_id
}

output "dead_letter_subscription_id" {
  value = module.pubsub.dead_letter_subscription_id
}

output "pubsub_service_agent" {
  value = module.pubsub.pubsub_service_agent
}

output "enabled_project_services" {
  value = module.project_services.enabled_services
}

output "artifact_registry_repository_url" {
  value = module.artifact_registry.docker_repository_url
}

output "image_api_service_account_email" {
  value = module.iam.image_api_service_account_email
}

output "image_worker_service_account_email" {
  value = module.iam.image_worker_service_account_email
}