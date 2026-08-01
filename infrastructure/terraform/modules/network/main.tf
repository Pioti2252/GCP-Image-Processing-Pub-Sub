resource "google_compute_network" "main" {
  project = var.project_id
  name    = var.network_name

  auto_create_subnetworks         = false
  routing_mode                    = "REGIONAL"
  delete_default_routes_on_create = false
}

resource "google_compute_subnetwork" "gke" {
  project = var.project_id
  region  = var.region
  name    = var.subnetwork_name
  network = google_compute_network.main.id

  ip_cidr_range = var.subnetwork_cidr

  private_ip_google_access = true

  secondary_ip_range {
    range_name    = var.pods_secondary_range_name
    ip_cidr_range = var.pods_secondary_cidr
  }

  secondary_ip_range {
    range_name    = var.services_secondary_range_name
    ip_cidr_range = var.services_secondary_cidr
  }

  dynamic "log_config" {
    for_each = var.enable_flow_logs ? [1] : []

    content {
      aggregation_interval = "INTERVAL_5_SEC"
      flow_sampling        = 0.5
      metadata             = "INCLUDE_ALL_METADATA"
    }
  }
}

resource "google_compute_router" "main" {
  project = var.project_id
  region  = var.region
  name    = "${var.network_name}-router"
  network = google_compute_network.main.id
}

resource "google_compute_address" "nat" {
  project = var.project_id
  region  = var.region
  name    = "${var.network_name}-nat-ip"

  address_type = "EXTERNAL"

  labels = var.labels

  lifecycle {
    create_before_destroy = true
  }
}

resource "google_compute_router_nat" "main" {
  project = var.project_id
  region  = var.region
  name    = "${var.network_name}-nat"
  router  = google_compute_router.main.name

  nat_ip_allocate_option = "MANUAL_ONLY"
  nat_ips                = [google_compute_address.nat.self_link]

  source_subnetwork_ip_ranges_to_nat = "LIST_OF_SUBNETWORKS"

  subnetwork {
    name = google_compute_subnetwork.gke.id

    source_ip_ranges_to_nat = [
      "ALL_IP_RANGES"
    ]
  }

  min_ports_per_vm = 64

  log_config {
    enable = true
    filter = "ERRORS_ONLY"
  }
}