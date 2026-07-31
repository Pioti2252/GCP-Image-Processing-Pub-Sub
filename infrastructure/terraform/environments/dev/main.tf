terraform {
  required_version = ">= 1.8.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 7.0"
    }

    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 7.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

provider "google-beta" {
  project = var.project_id
  region  = var.region
}

module "project_services" {
  source = "../../modules/project-services"

  project_id = var.project_id

  services = [
    "artifactregistry.googleapis.com",
    "compute.googleapis.com",
    "container.googleapis.com",
    "iam.googleapis.com",
    "iamcredentials.googleapis.com",
    "secretmanager.googleapis.com",
    "servicenetworking.googleapis.com",
    "sqladmin.googleapis.com",
    "storage.googleapis.com",
    "sts.googleapis.com",
    "monitoring.googleapis.com",
    "logging.googleapis.com"
  ]
}

module "artifact_registry" {
  source = "../../modules/artifact-registry"

  project_id    = var.project_id
  region        = var.region
  repository_id = "image-processing-dev"

  labels = {
    application = "image-processing"
    environment = "dev"
    managed_by  = "terraform"
  }

  depends_on = [
    module.project_services
  ]
}

module "pubsub" {
  source = "../../modules/pubsub"

  project_id = var.project_id

  topic_name                    = "image-jobs-dev"
  subscription_name             = "image-jobs-worker-dev"
  dead_letter_topic_name        = "image-jobs-dead-letter-dev"
  dead_letter_subscription_name = "image-jobs-dead-letter-monitor-dev"

  ack_deadline_seconds  = 60
  max_delivery_attempts = 5
  minimum_backoff       = "10s"
  maximum_backoff       = "600s"

  labels = {
    application = "image-processing"
    environment = "dev"
    managed_by  = "terraform"
  }
}

module "iam" {
  source = "../../modules/iam"

  project_id               = var.project_id
  environment              = "dev"
  pubsub_topic_name        = module.pubsub.topic_name
  worker_subscription_name = module.pubsub.worker_subscription_name

  depends_on = [
    module.project_services,
    module.pubsub
  ]
}