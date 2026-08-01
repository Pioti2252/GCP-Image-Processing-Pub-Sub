variable "project_id" {
  description = "ID projektu Google Cloud dla środowiska dev"
  type        = string
}

variable "region" {
  description = "Region Google Cloud"
  type        = string
  default     = "europe-central2"
}

variable "database_password" {
  description = "Hasło użytkownika aplikacyjnego Cloud SQL"
  type        = string
  sensitive   = true
}