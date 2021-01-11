# app-prereq-helm-charts

## Team Members
 - Leming Li | NUID:001054503 | li.lem@northeastern.edu
 - Sahana Kannahallimole Somanna | NUID:001086735 | kannahallimolesoma.s@northeastern.edu
 - Sahithi Sarabu | NUID:001067925 | sarabu.s@northeastern.edu

## This chart is to install the pre-requisites for our application which installs kafka and zookeeper
### Useful Helm Commands 
- To change the number of replicas of the running instances, change the value of replicaCount({{ .Values.statefulset.replicaCount }}) in values.yml 
- To Install the helm chart "helm install app-name ./app-path"
- To upgrade the helm chart "helm upgrade app-name ./app-path"
- To get history of the deployments "helm history app-name"
