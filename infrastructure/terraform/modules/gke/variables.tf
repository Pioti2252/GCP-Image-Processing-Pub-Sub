variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "location" {
  description = "Strefa albo region klastra GKE"
  type        = string
}

variable "environment" {
  description = "Nazwa środowiska"
  type        = string
}

variable "cluster_name" {
  description = "Nazwa klastra GKE"
  type        = string
}

variable "network_id" {
  description = "ID sieci VPC"
  type        = string
}

variable "subnetwork_id" {
  description = "ID subnetworku GKE"
  type        = string
}

variable "pods_secondary_range_name" {
  description = "Nazwa zakresu dodatkowego dla Podów"
  type        = string
}

variable "services_secondary_range_name" {
  description = "Nazwa zakresu dodatkowego dla Services"
  type        = string
}

variable "master_ipv4_cidr_block" {
  description = "Zakres adresów control plane GKE"
  type        = string
  default     = "172.16.0.0/28"
}

variable "master_authorized_networks" {
  description = "Zakresy CIDR uprawnione do dostępu do publicznego endpointu control plane"

  type = list(object({
    cidr_block   = string
    display_name = string
  }))

  default = []
}

variable "machine_type" {
  description = "Typ maszyny w node pool"
  type        = string
  default     = "e2-standard-2"
}

variable "disk_size_gb" {
  description = "Rozmiar dysku węzła"
  type        = number
  default     = 50
}

variable "min_node_count" {
  description = "Minimalna liczba węzłów"
  type        = number
  default     = 1
}

variable "max_node_count" {
  description = "Maksymalna liczba węzłów"
  type        = number
  default     = 3
}

variable "labels" {
  description = "Etykiety zasobów"
  type        = map(string)
  default     = {}
}