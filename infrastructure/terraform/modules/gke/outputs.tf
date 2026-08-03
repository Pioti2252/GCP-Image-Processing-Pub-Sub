output "cluster_name" {
  description = "Nazwa klastra GKE"
  value       = google_container_cluster.main.name
}

output "cluster_location" {
  description = "Lokalizacja klastra GKE"
  value       = google_container_cluster.main.location
}

output "cluster_endpoint" {
  description = "Endpoint control plane"
  value       = google_container_cluster.main.endpoint
  sensitive   = true
}

output "cluster_ca_certificate" {
  description = "Certyfikat CA klastra"
  value       = google_container_cluster.main.master_auth[0].cluster_ca_certificate
  sensitive   = true
}

output "workload_identity_pool" {
  description = "Workload Identity Pool klastra"
  value       = google_container_cluster.main.workload_identity_config[0].workload_pool
}

output "node_service_account_email" {
  description = "Konto serwisowe węzłów GKE"
  value       = google_service_account.gke_nodes.email
}