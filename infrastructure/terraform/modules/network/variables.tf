variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "region" {
  description = "Region zasobów sieciowych"
  type        = string
}

variable "environment" {
  description = "Nazwa środowiska"
  type        = string
}

variable "network_name" {
  description = "Nazwa sieci VPC"
  type        = string
}

variable "subnetwork_name" {
  description = "Nazwa subnetworku dla GKE"
  type        = string
}

variable "subnetwork_cidr" {
  description = "Podstawowy zakres adresów IP dla węzłów GKE"
  type        = string
}

variable "pods_secondary_range_name" {
  description = "Nazwa dodatkowego zakresu IP dla Podów"
  type        = string
}

variable "pods_secondary_cidr" {
  description = "Dodatkowy zakres IP dla Podów"
  type        = string
}

variable "services_secondary_range_name" {
  description = "Nazwa dodatkowego zakresu IP dla Services"
  type        = string
}

variable "services_secondary_cidr" {
  description = "Dodatkowy zakres IP dla Services"
  type        = string
}

variable "enable_flow_logs" {
  description = "Czy włączyć VPC Flow Logs"
  type        = bool
  default     = true
}

variable "labels" {
  description = "Etykiety obsługiwanych zasobów"
  type        = map(string)
  default     = {}
}