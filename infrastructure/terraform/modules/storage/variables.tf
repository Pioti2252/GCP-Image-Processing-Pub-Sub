variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "region" {
  description = "Lokalizacja bucketu"
  type        = string
}

variable "bucket_name" {
  description = "Globalnie unikalna nazwa bucketu"
  type        = string
}

variable "image_api_service_account_email" {
  description = "Konto serwisowe image-api"
  type        = string
}

variable "image_worker_service_account_email" {
  description = "Konto serwisowe image-worker"
  type        = string
}

variable "force_destroy" {
  description = "Czy Terraform może usunąć bucket zawierający obiekty"
  type        = bool
  default     = false
}

variable "labels" {
  description = "Etykiety zasobów"
  type        = map(string)
  default     = {}
}