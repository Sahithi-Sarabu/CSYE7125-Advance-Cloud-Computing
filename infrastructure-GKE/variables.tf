variable "project" {
  description = "The project in which to hold the components"
  type        = string
  default     = "my-first-project-296604"
}

variable "region" {
  description = "The region in which to create the VPC network"
  type        = string
  default     = "us-east1"
}

variable "zone" {
  description = "The zone in which to create the Kubernetes cluster. Must match the region"
  type        = string
  default     = "us-east1-b"
}

variable "node_num" {
  description = "The node number in each zone"
  type        = string
  default     = "2"
}


// Optional values that can be overridden or appended to if desired.
variable "cluster_name" {
  description = "The name to give the new Kubernetes cluster."
  type        = string
  default     = "gke-cluster"
}

variable "db_name" {
  description = "The name for the DB connection"
  type        = string
  default     = "csye7125"
}

variable "db_name_poller" {
  description = "The name for the DB connection"
  type        = string
  default     = "csye7125-poller"
}

variable "db_name_notifier" {
  description = "The name for the DB connection"
  type        = string
  default     = "csye7125-notifier"
}

variable "db_username" {
  description = "The user name for the DB connection"
  type        = string
  default     = "root"
}

variable "db_password" {
  description = "The password for the DB connection"
  type        = string
  default     = "11223344"
}


variable "project_services" {
  type = list

  default = [
    "cloudresourcemanager.googleapis.com",
    "servicenetworking.googleapis.com",
    "container.googleapis.com",
    "compute.googleapis.com",
    "iam.googleapis.com",
    "logging.googleapis.com",
    "monitoring.googleapis.com",
    "sqladmin.googleapis.com",
    "securetoken.googleapis.com",
  ]
  description = <<-EOF
  The GCP APIs that should be enabled in this project.
  EOF
}
