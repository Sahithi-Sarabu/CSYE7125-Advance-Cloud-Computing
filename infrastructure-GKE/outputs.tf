
// Used to identify the cluster in validate.sh.
output "cluster_name" {
  description = "Convenience output to obtain the GKE Cluster name"
  value       = google_container_cluster.cluster.name
}


// Used when setting up the GKE cluster to talk to Mysql.
output "mysql_instance" {
  description = "The generated name of the Cloud SQL instance"
  value       = google_sql_database_instance.default.name
}

// Full connection string for the Mysql DB
output "mysql_connection" {
  description = "The connection string dynamically generated for storage inside the Kubernetes configmap"
  value       = format("%s:%s:%s", data.google_client_config.current.project, var.region, google_sql_database_instance.default.name)
}

// Mysql DB username.
output "mysql_user" {
  description = "The Cloud SQL Instance User name"
  value       = google_sql_user.default.name
}

// Mysql DB password.
output "mysql_pass" {
  sensitive   = true
  description = "The Cloud SQL Instance Password (Generated)"
  value       = google_sql_user.default.password
}

output "cluster_endpoint" {
  description = "Cluster endpoint"
  value       = google_container_cluster.cluster.endpoint
}

output "cluster_ca_certificate" {
  sensitive   = true
  description = "Cluster ca certificate (base64 encoded)"
  value       = google_container_cluster.cluster.master_auth[0].cluster_ca_certificate
}

output "get_credentials" {
  description = "Gcloud get-credentials command"
  value       = format("gcloud container clusters get-credentials --project %s --region %s --internal-ip %s", var.project, var.region, var.cluster_name)
}