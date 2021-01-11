# infrastructure-aks
## Team Members
Name | NUID | email 
---|---|---
Leming Li | 001054503 | li.lem@northeastern.edu
Sahana Kannahallimole Somanna | 001086735 | kannahallimolesoma.s@northeastern.edu
Sahithi Sarabu | 001067925 | sarabu.s@northeastern.edu
## Prerequisites
### Cloud Project

If you do not have a Azure Cloud account, please signup for a free trial [here](https://portal.azure.com/). You'll need access to a Azure Cloud with billing enabled

### Tools
#### Install Ansible

Ansible is used to automate the manipulation of cloud infrastructure. Its [installation instructions](https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html) are also available online.

#### Install Cloud CLI

The Azure CLI is used to interact with your Azure resources. [Installation instructions](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) for multiple platforms are available online.

#### Install kubectl CLI

The kubectl CLI is used to interteract with both Kubernetes Engine and Kubernetes in general. [Installation instructions](https://cloud.google.com/kubernetes-engine/docs/quickstart) for multiple platforms are available online.

## Create Resources

To create the entire environment via Ansible, run the following command:

```console
ansible-playbook playbooks/setup.yml 
```
## Tear Down
To delete all created resources in Azure, run:

```console
ansible-playbook playbooks/delete.yml
```
## Deployment

We use Jenkins to deploy our applications to Azure.

### Authenticate gcloud

Prior to build the pipeline, ensure the jenkins server has authenticated the gcloud client by running the following commands:

```console
az account set --subscription <subscription_number>
az aks get-credentials --resource-group <rg_name> --name <cluster_name>
```
### Set up Jenkins

Same set up steps, except change `Jenkinsfile` to `Jenkinsfile_Aks`.

### Install prerequired charts

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