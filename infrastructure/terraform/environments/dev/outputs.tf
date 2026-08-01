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

output "network_name" {
  value = module.network.network_name
}

output "gke_subnetwork_name" {
  value = module.network.subnetwork_name
}

output "gke_pods_secondary_range_name" {
  value = module.network.pods_secondary_range_name
}

output "gke_services_secondary_range_name" {
  value = module.network.services_secondary_range_name
}

output "cloud_nat_ip_address" {
  value = module.network.nat_ip_address
}

output "image_storage_bucket_name" {
  value = module.storage.bucket_name
}

output "image_storage_bucket_url" {
  value = module.storage.bucket_url
}

output "cloud_sql_instance_name" {
  value = module.cloud_sql.instance_name
}

output "cloud_sql_connection_name" {
  value = module.cloud_sql.connection_name
}

output "cloud_sql_private_ip_address" {
  value = module.cloud_sql.private_ip_address
}

output "cloud_sql_database_name" {
  value = module.cloud_sql.database_name
}

output "database_password_secret_id" {
  value = module.secret_manager.database_password_secret_id
}