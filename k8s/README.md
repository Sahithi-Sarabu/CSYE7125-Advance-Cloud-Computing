# Ansible Playbooks for Kubernetes Cluster Setup & Teardown
## Prerequirements
- Configured AWS cli (aws account set-up with proper credentials/permissions/roles/region needed).
- Install Ansible
- export configured AWS_PROFILE
- Local ssh key ready on ~/.ssh You can generate it using ssh-keygen command if you don't have one already.
## Cluster Setup
- Manually create a public host zone for your cluster.
- Manually create as S3 bucket with unique name (domain name for your cluster) in us-east-1/us-west-2 region with encryption and bucket versioning enabled.
```shell
aws s3api create-bucket --bucket clustername --region us-east-1
aws s3api put-bucket-versioning --bucket clustername --versioning-configuration Status=Enabled
aws s3api put-bucket-encryption --bucket clustername --server-side-encryption-configuration ‘{“Rules”:[{“ApplyServerSideEncryptionByDefault”:{“SSEAlgorithm”:”AES256"}}]}’
```
Setup cluster using following command:
```shell
ansible-playbook playbooks/setup-k8s-cluster.yml --extra-vars "AWS_REGION=awsregion NAME=clustername NODE_SIZE=t2.micro MASTER_SIZE=t2.micro NODE_COUNT=3 SSH_KEY=yoursshkey.pub  K8S_VERSION=1.16.15" 
```
>**NOTE**: When validating the cluster, the cluster is getting ready after multiple validation failures. Please wait patiently.
- Verify Kubernetes cluster is ready for use with following commands 
    - ```kubectl``` get nodes should list all nodes and master nodes.

    - ```kops validate cluster``` should successfully validate the cluster.
## SSH into a compute node via Bastion
- Get the ELB address for our "bastions" instance group. We can check it with following command:
```shell
aws elb --output=table describe-load-balancers|grep DNSName.\*bastion|awk '{print $4}'
```
- Configure the config file in ```~/.ssh```. It should be look as below
```txt
Host private1
  Hostname computenodeaddress
  IdentityFile yoursshkey
  ProxyCommand ssh admin@bastion -W %h:%p

Host bastion
  Hostname elbaddress
  PubKeyAuthentication yes
  IdentityFile yoursshkey
```
- SSH into the compute node
```shell
ssh admin@private1
```
## Cluster Teardown
teardown cluster using following command:
```shell
ansible-playbook playbooks/delete-k8s-cluster.yml --extra-vars "NAME=clustername s3=bucketname AWS_PROFILE=profilename"
``` 
