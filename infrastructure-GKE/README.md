# infrastructure-GKE
## Team Members
Name | NUID | email 
---|---|---
Leming Li | 001054503 | li.lem@northeastern.edu
Sahana Kannahallimole Somanna | 001086735 | kannahallimolesoma.s@northeastern.edu
Sahithi Sarabu | 001067925 | sarabu.s@northeastern.edu
## Prerequisites
### Cloud Project

If you do not have a Google Cloud account, please signup for a free trial [here](https://cloud.google.com). You'll need access to a Google Cloud Project with billing enabled. See [Creating and Managing Projects](https://cloud.google.com/resource-manager/docs/creating-managing-projects) for creating a new project. To make cleanup easier it's recommended to create a new project.

### Required GCP APIs

The following APIs will be enabled:

* Compute Engine API
* Kubernetes Engine API
* Cloud SQL Admin API

### Tools
#### Install Terraform

Terraform is used to automate the manipulation of cloud infrastructure. Its [installation instructions](https://www.terraform.io/intro/getting-started/install.html) are also available online.

#### Install Cloud SDK

The Google Cloud SDK is used to interact with your GCP resources. [Installation instructions](https://cloud.google.com/sdk/downloads) for multiple platforms are available online.

#### Install kubectl CLI

The kubectl CLI is used to interteract with both Kubernetes Engine and Kubernetes in general. [Installation instructions](https://cloud.google.com/kubernetes-engine/docs/quickstart) for multiple platforms are available online.

## Create Resources

To create the entire environment via Terraform, run the following command:

```console
terraform apply -input=false -auto-approve 
```
## Tear Down
To delete all created resources in GCP, run:

```console
terraform destroy -input=false -auto-approve 
```
## Deployment

We use Jenkins to deploy our applications to GCP.

### Authenticate gcloud

Prior to build the pipeline, ensure the jenkins server has authenticated the gcloud client by running the following commands:

```console
gcloud init
gcloud auth application-default login
sudo cp -rf ~/.config/gcloud /var/lib/jenkins/.config
```
### Set up Jenkins

Same set up steps, except change `Jenkinsfile` to `Jenkinsfile_gke`.

### Install prerequired charts

Prior to use helm, ensure the local server has authenticated the gcloud client by running the following commands:
```console
gcloud init
gcloud auth application-default login
gcloud container clusters get-credentials [cluster_name] --region [region] --project [project_id]
```

We use kafka to send messages among the microservices, so we need to install it first.
```console
helm install zookeeper ./zookeeper
helm install kafka ./kafka
```
We use ingress chart to encrypt the ingress
```console
kubectl apply --validate=false -f https://github.com/jetstack/cert-manager/releases/download/v1.0.4/cert-manager.yaml
helm install ingress ./kubernetes-ingress
```