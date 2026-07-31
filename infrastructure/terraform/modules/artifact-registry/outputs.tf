output "repository_id" {
  description = "ID repozytorium Artifact Registry"
  value       = google_artifact_registry_repository.docker.repository_id
}

output "repository_name" {
  description = "Pełna nazwa repozytorium"
  value       = google_artifact_registry_repository.docker.name
}

output "repository_location" {
  description = "Region repozytorium"
  value       = google_artifact_registry_repository.docker.location
}

output "docker_repository_url" {
  description = "Adres repozytorium używany do tagowania obrazów Docker"
  value = format(
    "%s-docker.pkg.dev/%s/%s",
    google_artifact_registry_repository.docker.location,
    var.project_id,
    google_artifact_registry_repository.docker.repository_id
  )
}