resource "google_storage_bucket" "images" {
  project  = var.project_id
  name     = var.bucket_name
  location = var.region

  storage_class = "STANDARD"
  force_destroy = var.force_destroy

  uniform_bucket_level_access = true
  public_access_prevention    = "enforced"

  versioning {
    enabled = true
  }

  lifecycle_rule {
    condition {
      age            = 30
      matches_prefix = ["uploads/"]
    }

    action {
      type = "Delete"
    }
  }

  lifecycle_rule {
    condition {
      age            = 90
      matches_prefix = ["processed/"]
    }

    action {
      type = "Delete"
    }
  }

  lifecycle_rule {
    condition {
      days_since_noncurrent_time = 7
    }

    action {
      type = "Delete"
    }
  }

  labels = var.labels
}

resource "google_storage_bucket_iam_member" "image_api_object_user" {
  bucket = google_storage_bucket.images.name
  role   = "roles/storage.objectUser"

  member = "serviceAccount:${var.image_api_service_account_email}"
}

resource "google_storage_bucket_iam_member" "image_worker_object_user" {
  bucket = google_storage_bucket.images.name
  role   = "roles/storage.objectUser"

  member = "serviceAccount:${var.image_worker_service_account_email}"
}