data "google_project" "current" {
  project_id = var.project_id
}

resource "google_project_service" "pubsub" {
  count = var.manage_project_service ? 1 : 0

  project = var.project_id
  service = "pubsub.googleapis.com"

  disable_on_destroy = false
}

resource "google_project_service_identity" "pubsub" {
  count = var.manage_project_service ? 1 : 0

  provider = google-beta

  project = var.project_id
  service = "pubsub.googleapis.com"

  depends_on = [
    google_project_service.pubsub
  ]
}

locals {
  pubsub_service_agent_email = var.manage_project_service ? google_project_service_identity.pubsub[0].email : "service-${data.google_project.current.number}@gcp-sa-pubsub.iam.gserviceaccount.com"
}

resource "google_pubsub_topic" "image_jobs" {
  project = var.project_id
  name    = var.topic_name
  labels  = var.labels

  depends_on = [
    google_project_service.pubsub
  ]
}

resource "google_pubsub_topic" "dead_letter" {
  project = var.project_id
  name    = var.dead_letter_topic_name
  labels  = var.labels

  depends_on = [
    google_project_service.pubsub
  ]
}

resource "google_pubsub_topic_iam_member" "dead_letter_publisher" {
  project = var.project_id
  topic   = google_pubsub_topic.dead_letter.name
  role    = "roles/pubsub.publisher"

  member = format(
    "serviceAccount:%s",
    local.pubsub_service_agent_email
  )
}

resource "google_pubsub_subscription" "worker" {
  project = var.project_id
  name    = var.subscription_name
  topic   = google_pubsub_topic.image_jobs.id

  ack_deadline_seconds       = var.ack_deadline_seconds
  message_retention_duration = var.message_retention_duration

  expiration_policy {
    ttl = ""
  }

  retry_policy {
    minimum_backoff = var.minimum_backoff
    maximum_backoff = var.maximum_backoff
  }

  dead_letter_policy {
    dead_letter_topic     = google_pubsub_topic.dead_letter.id
    max_delivery_attempts = var.max_delivery_attempts
  }

  labels = var.labels

  depends_on = [
    google_pubsub_topic_iam_member.dead_letter_publisher
  ]
}

resource "google_pubsub_subscription_iam_member" "worker_subscriber" {
  project      = var.project_id
  subscription = google_pubsub_subscription.worker.name
  role         = "roles/pubsub.subscriber"

  member = format(
    "serviceAccount:%s",
    local.pubsub_service_agent_email
  )
}

resource "google_pubsub_subscription" "dead_letter_monitor" {
  project = var.project_id
  name    = var.dead_letter_subscription_name
  topic   = google_pubsub_topic.dead_letter.id

  ack_deadline_seconds       = var.ack_deadline_seconds
  message_retention_duration = var.message_retention_duration

  expiration_policy {
    ttl = ""
  }

  labels = var.labels
}