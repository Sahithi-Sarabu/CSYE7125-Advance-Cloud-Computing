// Enable required services on the project
resource "google_project_service" "service" {
  count   = length(var.project_services)
  project = var.project
  service = element(var.project_services, count.index)

  // Do not disable the service on destroy. On destroy, we are going to
  // destroy the project, but we need the APIs available to destroy the
  // underlying resources.
  disable_on_destroy = false
}

// Create a network for GKE
resource "google_compute_network" "gke_network" {
  name                    = format("%s-network", var.cluster_name)
  project                 = var.project
  auto_create_subnetworks = false

  depends_on = [
    "google_project_service.service",
  ]
}

// Create GKE subnets
resource "google_compute_subnetwork" "gke_subnetwork" {
  name          = format("%s-subnet", var.cluster_name)
  project       = var.project
  network       = google_compute_network.gke_network.self_link
  region        = var.region
  ip_cidr_range = "10.0.0.0/24"
  secondary_ip_range {
    range_name    = format("%s-pod-range", var.cluster_name)
    ip_cidr_range = "10.1.0.0/16"
  }

  secondary_ip_range {
    range_name    = format("%s-svc-range", var.cluster_name)
    ip_cidr_range = "10.2.0.0/20"
  }
}

// Create GKE firewall
resource "google_compute_firewall" "gke-firewall" {
  name    = "gke-ssh"
  network = google_compute_network.gke_network.name
  direction     = "INGRESS"
  project       = var.project
  source_ranges = ["0.0.0.0/0"]
  allow {
    protocol = "tcp"
    ports    = ["22"]
  }
}

// Create a network for database
resource "google_compute_network" "database_network" {
  name                    = "db-network"
  project                 = var.project
  auto_create_subnetworks = false
  depends_on = [
    "google_project_service.service",
  ]
}

// Create database subnets
resource "google_compute_subnetwork" "database_subnetwork" {
  name          = "db-subnet"
  project       = var.project
  network       = google_compute_network.database_network.self_link
  region        = var.region
  ip_cidr_range = "172.16.0.0/16"
}


//Create VPC peering
resource "google_compute_global_address" "private_ip_address" {
  provider = "google-beta"

  name          = format("%s-priv-ip", var.db_name)
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 24
  network       = google_compute_network.gke_network.self_link
}

resource "google_service_networking_connection" "private_vpc_connection" {
  provider = "google-beta"

  network                 = google_compute_network.gke_network.self_link
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip_address.name]
}


