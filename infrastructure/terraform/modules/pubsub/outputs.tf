output "topic_id" {
  description = "Identyfikator głównego topicu"
  value       = google_pubsub_topic.image_jobs.id
}

output "topic_name" {
  description = "Nazwa głównego topicu"
  value       = google_pubsub_topic.image_jobs.name
}

output "worker_subscription_id" {
  description = "Identyfikator subskrypcji workera"
  value       = google_pubsub_subscription.worker.id
}

output "worker_subscription_name" {
  description = "Nazwa subskrypcji workera"
  value       = google_pubsub_subscription.worker.name
}

output "dead_letter_topic_id" {
  description = "Identyfikator dead-letter topicu"
  value       = google_pubsub_topic.dead_letter.id
}

output "dead_letter_subscription_id" {
  description = "Identyfikator subskrypcji DLQ"
  value       = google_pubsub_subscription.dead_letter_monitor.id
}

output "pubsub_service_agent" {
  description = "Email Google-managed Pub/Sub service agent"
  value       = local.pubsub_service_agent_email
}