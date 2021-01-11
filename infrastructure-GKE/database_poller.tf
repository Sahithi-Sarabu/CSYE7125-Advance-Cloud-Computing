resource "random_id" "db_name_suffix_poller" {
  byte_length = 3
}

resource "google_sql_database_instance" "poller" {
  project          = var.project
  name             = format("%s-mysql-%s", var.db_name_poller, random_id.db_name_suffix_poller.hex)
  database_version = "MYSQL_5_7"
  region           = var.region

  depends_on = [
    "google_service_networking_connection.private_vpc_connection"
  ]

  settings {
    tier              = "db-f1-micro"
    activation_policy = "ALWAYS"
    availability_type = "ZONAL"

    ip_configuration {
      ipv4_enabled    = "false"
      private_network = google_compute_network.gke_network.self_link
    }

    disk_autoresize = false
    disk_size       = "10"
    disk_type       = "PD_SSD"
    pricing_plan    = "PER_USE"

    location_preference {
      zone = var.zone
    }
  }

  timeouts {
    create = "10m"
    update = "10m"
    delete = "10m"
  }
}

resource "google_sql_database" "poller" {
  name       = "csye7125_poller"
  project    = var.project
  instance   = google_sql_database_instance.poller.name
  depends_on = ["google_sql_database_instance.poller"]
}


resource "google_sql_user" "poller" {
  name       = var.db_username
  project    = var.project
  instance   = google_sql_database_instance.poller.name
  password   = var.db_password
  depends_on = ["google_sql_database_instance.poller"]
}
