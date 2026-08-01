output "network_id" {
  description = "Identyfikator sieci VPC"
  value       = google_compute_network.main.id
}

output "network_name" {
  description = "Nazwa sieci VPC"
  value       = google_compute_network.main.name
}

output "network_self_link" {
  description = "Pełny adres sieci VPC"
  value       = google_compute_network.main.self_link
}

output "subnetwork_id" {
  description = "Identyfikator subnetworku GKE"
  value       = google_compute_subnetwork.gke.id
}

output "subnetwork_name" {
  description = "Nazwa subnetworku GKE"
  value       = google_compute_subnetwork.gke.name
}

output "subnetwork_self_link" {
  description = "Pełny adres subnetworku GKE"
  value       = google_compute_subnetwork.gke.self_link
}

output "pods_secondary_range_name" {
  description = "Nazwa zakresu IP dla Podów"
  value       = var.pods_secondary_range_name
}

output "services_secondary_range_name" {
  description = "Nazwa zakresu IP dla Services"
  value       = var.services_secondary_range_name
}

output "nat_ip_address" {
  description = "Stały publiczny adres IP używany przez Cloud NAT"
  value       = google_compute_address.nat.address
}

output "router_name" {
  description = "Nazwa Cloud Routera"
  value       = google_compute_router.main.name
}