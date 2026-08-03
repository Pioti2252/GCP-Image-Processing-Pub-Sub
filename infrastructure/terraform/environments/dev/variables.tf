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

variable "zone" {
  description = "Strefa Google Cloud dla klastra GKE dev"
  type        = string
  default     = "europe-central2-a"
}

variable "gke_admin_cidr" {
  description = "Publiczny adres IP administratora w formacie CIDR /32"
  type        = string

  validation {
    condition     = can(cidrhost(var.gke_admin_cidr, 0))
    error_message = "gke_admin_cidr musi być poprawnym zakresem CIDR, np. 83.20.10.15/32."
  }
}