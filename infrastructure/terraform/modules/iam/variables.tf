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