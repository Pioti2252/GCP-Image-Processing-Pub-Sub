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