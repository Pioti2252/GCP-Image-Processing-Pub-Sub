variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "region" {
  description = "Lokalizacja bucketu ze stanem Terraform"
  type        = string
  default     = "europe-central2"
}