variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "topic_name" {
  description = "Nazwa głównego topicu Pub/Sub"
  type        = string
  default     = "image-jobs"
}

variable "subscription_name" {
  description = "Nazwa subskrypcji używanej przez image-worker"
  type        = string
  default     = "image-jobs-worker"
}

variable "dead_letter_topic_name" {
  description = "Nazwa topicu dla wiadomości nieprzetworzonych"
  type        = string
  default     = "image-jobs-dead-letter"
}

variable "dead_letter_subscription_name" {
  description = "Nazwa subskrypcji podpiętej do dead-letter topicu"
  type        = string
  default     = "image-jobs-dead-letter-monitor"
}

variable "ack_deadline_seconds" {
  description = "Czas na potwierdzenie przetworzenia wiadomości"
  type        = number
  default     = 60

  validation {
    condition = (
      var.ack_deadline_seconds >= 10 &&
      var.ack_deadline_seconds <= 600
    )

    error_message = "ack_deadline_seconds musi mieścić się w zakresie 10–600 sekund."
  }
}

variable "max_delivery_attempts" {
  description = "Maksymalna liczba prób przed przekazaniem wiadomości do DLQ"
  type        = number
  default     = 5

  validation {
    condition = (
      var.max_delivery_attempts >= 5 &&
      var.max_delivery_attempts <= 100
    )

    error_message = "max_delivery_attempts musi mieścić się w zakresie 5–100."
  }
}

variable "minimum_backoff" {
  description = "Minimalny czas pomiędzy próbami dostarczenia"
  type        = string
  default     = "10s"
}

variable "maximum_backoff" {
  description = "Maksymalny czas pomiędzy próbami dostarczenia"
  type        = string
  default     = "600s"
}

variable "message_retention_duration" {
  description = "Czas przechowywania niepotwierdzonych wiadomości"
  type        = string
  default     = "604800s"
}

variable "labels" {
  description = "Etykiety zasobów"
  type        = map(string)
  default     = {}
}