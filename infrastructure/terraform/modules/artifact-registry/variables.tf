variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "region" {
  description = "Region repozytorium Artifact Registry"
  type        = string
}

variable "repository_id" {
  description = "Nazwa repozytorium Docker"
  type        = string
}

variable "description" {
  description = "Opis repozytorium"
  type        = string
  default     = "Docker images for image-processing application"
}

variable "labels" {
  description = "Etykiety repozytorium"
  type        = map(string)
  default     = {}
}