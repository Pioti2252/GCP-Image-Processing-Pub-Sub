variable "project_id" {
  description = "ID projektu Google Cloud"
  type        = string
}

variable "services" {
  description = "Lista API Google Cloud, które mają zostać włączone"
  type        = set(string)
}