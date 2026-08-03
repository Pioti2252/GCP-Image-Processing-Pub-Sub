resource "google_service_account" "image_api" {
  project = var.project_id

  account_id = "image-api-${var.environment}"
  display_name = format(
    "Image API %s",
    upper(var.environment)
  )

  description = "Runtime identity for image-api in ${var.environment}"
}

resource "google_service_account" "image_worker" {
  project = var.project_id

  account_id = "image-worker-${var.environment}"
  display_name = format(
    "Image Worker %s",
    upper(var.environment)
  )

  description = "Runtime identity for image-worker in ${var.environment}"
}

resource "google_pubsub_topic_iam_member" "image_api_publisher" {
  project = var.project_id
  topic   = var.pubsub_topic_name
  role    = "roles/pubsub.publisher"

  member = "serviceAccount:${google_service_account.image_api.email}"
}

resource "google_pubsub_subscription_iam_member" "image_worker_subscriber" {
  project      = var.project_id
  subscription = var.worker_subscription_name
  role         = "roles/pubsub.subscriber"

  member = "serviceAccount:${google_service_account.image_worker.email}"
}

resource "google_project_iam_member" "image_api_cloud_sql_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"

  member = "serviceAccount:${google_service_account.image_api.email}"
}

resource "google_project_iam_member" "image_worker_cloud_sql_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"

  member = "serviceAccount:${google_service_account.image_worker.email}"
}

resource "google_service_account_iam_member" "image_api_workload_identity" {
  service_account_id = google_service_account.image_api.name
  role               = "roles/iam.workloadIdentityUser"

  member = format(
    "serviceAccount:%s.svc.id.goog[%s/%s]",
    var.project_id,
    var.kubernetes_namespace,
    var.image_api_kubernetes_service_account
  )
}

resource "google_service_account_iam_member" "image_worker_workload_identity" {
  service_account_id = google_service_account.image_worker.name
  role               = "roles/iam.workloadIdentityUser"

  member = format(
    "serviceAccount:%s.svc.id.goog[%s/%s]",
    var.project_id,
    var.kubernetes_namespace,
    var.image_worker_kubernetes_service_account
  )
}