terraform {
  required_version = ">= 1.8.0"

  backend "gcs" {
    bucket = "gcp-image-pub-sub-terraform-state"
    prefix = "environments/dev"
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

module "network" {
  source = "../../modules/network"

  project_id  = var.project_id
  region      = var.region
  environment = "dev"

  network_name    = "image-processing-dev-vpc"
  subnetwork_name = "image-processing-dev-gke-subnet"

  subnetwork_cidr = "10.10.0.0/20"

  pods_secondary_range_name = "image-processing-dev-pods"
  pods_secondary_cidr       = "10.20.0.0/16"

  services_secondary_range_name = "image-processing-dev-services"
  services_secondary_cidr       = "10.30.0.0/20"

  enable_flow_logs = true

  labels = {
    application = "image-processing"
    environment = "dev"
    managed_by  = "terraform"
  }

  depends_on = [
    module.project_services
  ]
}

module "gke" {
  source = "../../modules/gke"

  project_id  = var.project_id
  location    = var.zone
  environment = "dev"

  cluster_name = "image-processing-dev-gke"

  network_id    = module.network.network_id
  subnetwork_id = module.network.subnetwork_id

  pods_secondary_range_name = (
    module.network.pods_secondary_range_name
  )

  services_secondary_range_name = (
    module.network.services_secondary_range_name
  )

  master_ipv4_cidr_block = "172.16.0.0/28"

  master_authorized_networks = [
    {
      cidr_block   = var.gke_admin_cidr
      display_name = "administrator"
    }
  ]

  machine_type  = "e2-standard-2"
  disk_size_gb  = 50
  min_node_count = 1
  max_node_count = 3

  labels = {
    application = "image-processing"
    environment = "dev"
    managed_by  = "terraform"
  }

  depends_on = [
    module.project_services,
    module.network,
    module.artifact_registry
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

module "cloud_sql" {
  source = "../../modules/cloud-sql"

  project_id  = var.project_id
  region      = var.region
  environment = "dev"

  network_id = module.network.network_id

  instance_name = "image-processing-dev-postgres"

  database_version = "POSTGRES_17"
  tier             = "db-custom-1-3840"

  database_name     = "image_processing"
  database_user     = "image_app"
  database_password = var.database_password

  private_services_range_prefix_length = 16
  deletion_protection                  = true

  labels = {
    application = "image-processing"
    environment = "dev"
    managed_by  = "terraform"
  }

  depends_on = [
    module.project_services,
    module.network
  ]
}

module "secret_manager" {
  source = "../../modules/secret-manager"

  project_id  = var.project_id
  environment = "dev"

  database_password = var.database_password

  image_api_service_account_email = (
    module.iam.image_api_service_account_email
  )

  image_worker_service_account_email = (
    module.iam.image_worker_service_account_email
  )

  labels = {
    application = "image-processing"
    environment = "dev"
    managed_by  = "terraform"
  }

  depends_on = [
    module.project_services,
    module.iam,
    module.cloud_sql
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
  
  kubernetes_namespace = "image-processing"

  image_api_kubernetes_service_account    = "image-api"
  image_worker_kubernetes_service_account = "image-worker"

  depends_on = [
    module.project_services,
    module.pubsub
  ]

}

module "storage" {
  source = "../../modules/storage"

  project_id = var.project_id
  region     = var.region

  bucket_name = "${var.project_id}-image-processing-dev"

  image_api_service_account_email = (
    module.iam.image_api_service_account_email
  )

  image_worker_service_account_email = (
    module.iam.image_worker_service_account_email
  )

  force_destroy = false

  labels = {
    application = "image-processing"
    environment = "dev"
    managed_by  = "terraform"
  }

  depends_on = [
    module.project_services,
    module.iam
  ]
}