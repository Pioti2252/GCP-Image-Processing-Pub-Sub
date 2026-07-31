output "image_api_service_account_email" {
  description = "Adres konta serwisowego image-api"
  value       = google_service_account.image_api.email
}

output "image_api_service_account_name" {
  description = "Pełna nazwa konta serwisowego image-api"
  value       = google_service_account.image_api.name
}

output "image_worker_service_account_email" {
  description = "Adres konta serwisowego image-worker"
  value       = google_service_account.image_worker.email
}

output "image_worker_service_account_name" {
  description = "Pełna nazwa konta serwisowego image-worker"
  value       = google_service_account.image_worker.name
}