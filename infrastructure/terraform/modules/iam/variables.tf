variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "environment" {
  description = "Nazwa środowiska"
  type        = string
}

variable "pubsub_topic_name" {
  description = "Nazwa topicu publikowanego przez image-api"
  type        = string
}

variable "worker_subscription_name" {
  description = "Nazwa subskrypcji używanej przez image-worker"
  type        = string
}

variable "kubernetes_namespace" {
  description = "Namespace Kubernetes, w którym działają aplikacje"
  type        = string
  default     = "image-processing"
}

variable "image_api_kubernetes_service_account" {
  description = "Nazwa Kubernetes ServiceAccount dla image-api"
  type        = string
  default     = "image-api"
}

variable "image_worker_kubernetes_service_account" {
  description = "Nazwa Kubernetes ServiceAccount dla image-worker"
  type        = string
  default     = "image-worker"
}