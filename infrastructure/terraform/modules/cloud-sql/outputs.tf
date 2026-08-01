output "instance_name" {
  description = "Nazwa instancji Cloud SQL"
  value       = google_sql_database_instance.postgres.name
}

output "connection_name" {
  description = "Connection name używany przez Cloud SQL Auth Proxy"
  value       = google_sql_database_instance.postgres.connection_name
}

output "private_ip_address" {
  description = "Prywatny adres IP instancji Cloud SQL"
  value       = google_sql_database_instance.postgres.private_ip_address
}

output "database_name" {
  description = "Nazwa bazy danych aplikacji"
  value       = google_sql_database.application.name
}

output "database_user" {
  description = "Nazwa użytkownika bazy"
  value       = google_sql_user.application.name
}