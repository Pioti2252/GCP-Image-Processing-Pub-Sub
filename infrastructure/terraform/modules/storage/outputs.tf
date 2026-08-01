output "bucket_name" {
  description = "Nazwa bucketu na obrazy"
  value       = google_storage_bucket.images.name
}

output "bucket_url" {
  description = "Adres bucketu w formacie gs://"
  value       = "gs://${google_storage_bucket.images.name}"
}

output "bucket_self_link" {
  description = "Pełny adres zasobu bucketu"
  value       = google_storage_bucket.images.self_link
}