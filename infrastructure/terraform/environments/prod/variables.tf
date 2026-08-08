variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "region" {
  description = "Region Google Cloud"
  type        = string
}

variable "database_password" {
  description = "Hasło użytkownika bazy danych PROD"
  type        = string
  sensitive   = true
}