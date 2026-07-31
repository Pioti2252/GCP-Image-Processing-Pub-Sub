output "enabled_services" {
  description = "Lista API zarządzanych przez Terraform"
  value       = sort(keys(google_project_service.services))
}