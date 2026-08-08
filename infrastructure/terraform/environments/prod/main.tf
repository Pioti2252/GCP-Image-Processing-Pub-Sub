terraform {
  required_version = ">= 1.8.0"

  backend "gcs" {
    bucket = "gcp-image-pub-sub-terraform-state"
    prefix = "environments/prod"
  }

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

# PROD korzysta z istniejącego VPC utworzonego przez środowisko DEV.
# Dzięki remote_state nie tworzymy drugiej sieci.
data "terraform_remote_state" "dev" {
  backend = "gcs"

  config = {
    bucket = "gcp-image-pub-sub-terraform-state"
    prefix = "environments/dev"
  }
}

module "pubsub" {
  source = "../../modules/pubsub"

  project_id = var.project_id

  # Pub/Sub API i Google-managed service identity
  # są już zarządzane przez DEV.
  manage_project_service = false

  topic_name                    = "image-jobs-prod"
  subscription_name             = "image-jobs-worker-prod"
  dead_letter_topic_name        = "image-jobs-dead-letter-prod"
  dead_letter_subscription_name = "image-jobs-dead-letter-monitor-prod"

  ack_deadline_seconds  = 60
  max_delivery_attempts = 5
  minimum_backoff       = "10s"
  maximum_backoff       = "600s"

  labels = {
    application = "image-processing"
    environment = "prod"
    managed_by  = "terraform"
  }
}

module "iam" {
  source = "../../modules/iam"

  project_id  = var.project_id
  environment = "prod"

  pubsub_topic_name        = module.pubsub.topic_name
  worker_subscription_name = module.pubsub.worker_subscription_name

  kubernetes_namespace = "image-processing-prod"

  image_api_kubernetes_service_account    = "image-api"
  image_worker_kubernetes_service_account = "image-worker"

  depends_on = [
    module.pubsub
  ]
}

module "cloud_sql" {
  source = "../../modules/cloud-sql"

  project_id  = var.project_id
  region      = var.region
  environment = "prod"

  # Używamy VPC utworzonego przez DEV.
  network_id = data.terraform_remote_state.dev.outputs.network_id

  instance_name = "image-processing-prod-postgres"

  database_version = "POSTGRES_17"
  tier             = "db-custom-1-3840"

  database_name     = "image_processing"
  database_user     = "image_app"
  database_password = var.database_password

  # Private Service Access już istnieje w tym VPC.
  create_private_service_connection = false

  private_services_range_prefix_length = 16

  deletion_protection = true

  labels = {
    application = "image-processing"
    environment = "prod"
    managed_by  = "terraform"
  }
}

module "storage" {
  source = "../../modules/storage"

  project_id = var.project_id
  region     = var.region

  bucket_name = "${var.project_id}-image-processing-prod"

  image_api_service_account_email = (
    module.iam.image_api_service_account_email
  )

  image_worker_service_account_email = (
    module.iam.image_worker_service_account_email
  )

  force_destroy = false

  labels = {
    application = "image-processing"
    environment = "prod"
    managed_by  = "terraform"
  }

  depends_on = [
    module.iam
  ]
}

module "secret_manager" {
  source = "../../modules/secret-manager"

  project_id  = var.project_id
  environment = "prod"

  database_password = var.database_password

  image_api_service_account_email = (
    module.iam.image_api_service_account_email
  )

  image_worker_service_account_email = (
    module.iam.image_worker_service_account_email
  )

  labels = {
    application = "image-processing"
    environment = "prod"
    managed_by  = "terraform"
  }

  depends_on = [
    module.iam,
    module.cloud_sql
  ]
}