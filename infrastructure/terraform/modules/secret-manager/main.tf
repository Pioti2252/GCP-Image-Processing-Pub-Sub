resource "google_secret_manager_secret" "database_password" {
  project   = var.project_id
  secret_id = "image-processing-${var.environment}-database-password"

  replication {
    auto {}
  }

  labels = var.labels
}

resource "google_secret_manager_secret_version" "database_password" {
  secret      = google_secret_manager_secret.database_password.id
  secret_data = var.database_password
}

resource "google_secret_manager_secret_iam_member" "image_api_accessor" {
  project   = var.project_id
  secret_id = google_secret_manager_secret.database_password.secret_id
  role      = "roles/secretmanager.secretAccessor"

  member = "serviceAccount:${var.image_api_service_account_email}"
}

resource "google_secret_manager_secret_iam_member" "image_worker_accessor" {
  project   = var.project_id
  secret_id = google_secret_manager_secret.database_password.secret_id
  role      = "roles/secretmanager.secretAccessor"

  member = "serviceAccount:${var.image_worker_service_account_email}"
}