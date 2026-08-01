variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "environment" {
  description = "Nazwa środowiska"
  type        = string
}

variable "database_password" {
  description = "Hasło użytkownika aplikacyjnego Cloud SQL"
  type        = string
  sensitive   = true
}

variable "image_api_service_account_email" {
  description = "Konto serwisowe image-api"
  type        = string
}

variable "image_worker_service_account_email" {
  description = "Konto serwisowe image-worker"
  type        = string
}

variable "labels" {
  description = "Etykiety Secret Manager"
  type        = map(string)
  default     = {}
}