variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "region" {
  description = "Region instancji Cloud SQL"
  type        = string
}

variable "environment" {
  description = "Nazwa środowiska"
  type        = string
}

variable "network_id" {
  description = "ID sieci VPC używanej przez Cloud SQL"
  type        = string
}

variable "instance_name" {
  description = "Nazwa instancji Cloud SQL"
  type        = string
}

variable "database_version" {
  description = "Wersja PostgreSQL"
  type        = string
  default     = "POSTGRES_17"
}

variable "tier" {
  description = "Typ maszyny Cloud SQL"
  type        = string
  default     = "db-custom-1-3840"
}

variable "database_name" {
  description = "Nazwa bazy danych aplikacji"
  type        = string
  default     = "image_processing"
}

variable "database_user" {
  description = "Nazwa użytkownika bazy danych"
  type        = string
  default     = "image_app"
}

variable "database_password" {
  description = "Hasło użytkownika bazy danych"
  type        = string
  sensitive   = true
}

variable "private_services_range_prefix_length" {
  description = "Długość prefiksu zakresu Private Services Access"
  type        = number
  default     = 16
}

variable "deletion_protection" {
  description = "Ochrona instancji przed przypadkowym usunięciem"
  type        = bool
  default     = true
}

variable "labels" {
  description = "Etykiety instancji"
  type        = map(string)
  default     = {}
}