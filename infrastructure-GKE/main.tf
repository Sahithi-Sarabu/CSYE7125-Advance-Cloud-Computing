resource "google_container_cluster" "cluster" {
  provider = google-beta

  name     = var.cluster_name
  project  = var.project
  location = var.region

  network    = google_compute_network.gke_network.self_link
  subnetwork = google_compute_subnetwork.gke_subnetwork.self_link

  logging_service    = "logging.googleapis.com/kubernetes"
  monitoring_service = "monitoring.googleapis.com/kubernetes"
  remove_default_node_pool = "true"
  initial_node_count       = 2

  // Configure various addons
  addons_config {
    // Enable network policy (Calico)
    network_policy_config {
      disabled = false
    }
  }

  // Disable basic authentication and cert-based authentication.
  // Empty fields for username and password are how to "disable" the
  // credentials from being generated.
  master_auth {
    username = ""
    password = ""

    client_certificate_config {
      issue_client_certificate = "false"
    }
  }

  // Enable network policy configurations (like Calico) - for some reason this
  // has to be in here twice.
  network_policy {
    enabled = "true"
  }

  // Allocate IPs in our subnetwork
  ip_allocation_policy {
    use_ip_aliases                = true
    cluster_secondary_range_name  = google_compute_subnetwork.gke_subnetwork.secondary_ip_range.0.range_name
    services_secondary_range_name = google_compute_subnetwork.gke_subnetwork.secondary_ip_range.1.range_name
  }

  // Allow plenty of time for each operation to finish (default was 10m)
  timeouts {
    create = "20m"
    update = "20m"
    delete = "20m"
  }

  depends_on = [
    "google_project_service.service",
    "google_compute_network.gke_network",
    "google_compute_subnetwork.gke_subnetwork"
  ]

}
resource "google_container_node_pool" "np" {
  provider = "google-beta"

  name       = "node-pool"
  location   = var.region
  cluster    = google_container_cluster.cluster.name
  node_count = var.node_num

  // Repair any issues but don't auto upgrade node versions
  management {
    auto_repair  = "true"
    auto_upgrade = "false"
  }

  node_config {
    machine_type = "n1-standard-2"
    disk_type    = "pd-ssd"
    disk_size_gb = 30
    image_type   = "Ubuntu"

    // Use the minimal oauth scopes needed
    oauth_scopes = [
      "https://www.googleapis.com/auth/devstorage.read_only",
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
      "https://www.googleapis.com/auth/servicecontrol",
      "https://www.googleapis.com/auth/service.management.readonly",
      "https://www.googleapis.com/auth/trace.append",
    ]
  }

  depends_on = [
    "google_container_cluster.cluster",
  ]
}


