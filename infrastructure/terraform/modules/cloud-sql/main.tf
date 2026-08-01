resource "google_compute_global_address" "private_services" {
  project = var.project_id

  name          = "${var.instance_name}-private-services-range"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = var.private_services_range_prefix_length
  network       = var.network_id
}

resource "google_service_networking_connection" "private_vpc_connection" {
  network = var.network_id
  service = "servicenetworking.googleapis.com"

  reserved_peering_ranges = [
    google_compute_global_address.private_services.name
  ]
}

resource "google_sql_database_instance" "postgres" {
  project = var.project_id
  region  = var.region

  name             = var.instance_name
  database_version = var.database_version

  deletion_protection = var.deletion_protection

  settings {
    tier              = var.tier
    availability_type = "ZONAL"
    disk_type         = "PD_SSD"
    disk_size         = 20
    disk_autoresize   = true

    edition = "ENTERPRISE"

    ip_configuration {
      ipv4_enabled    = false
      private_network = var.network_id

      enable_private_path_for_google_cloud_services = true
    }

    backup_configuration {
      enabled                        = true
      start_time                     = "03:00"
      point_in_time_recovery_enabled = true
      transaction_log_retention_days = 7

      backup_retention_settings {
        retained_backups = 7
        retention_unit   = "COUNT"
      }
    }

    maintenance_window {
      day          = 7
      hour         = 4
      update_track = "stable"
    }

    database_flags {
      name  = "log_min_duration_statement"
      value = "1000"
    }

    database_flags {
      name  = "log_connections"
      value = "on"
    }

    user_labels = var.labels
  }

  depends_on = [
    google_service_networking_connection.private_vpc_connection
  ]
}

resource "google_sql_database" "application" {
  project  = var.project_id
  instance = google_sql_database_instance.postgres.name
  name     = var.database_name

  charset   = "UTF8"
  collation = "en_US.UTF8"
}

resource "google_sql_user" "application" {
  project  = var.project_id
  instance = google_sql_database_instance.postgres.name

  name     = var.database_user
  password = var.database_password
}