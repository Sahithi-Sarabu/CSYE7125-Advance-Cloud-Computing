# infrastructure
## Team Members
 - Leming Li | NUID:001054503 | li.lem@northeastern.edu
 - Sahana Kannahallimole Somanna | NUID:001086735 | kannahallimolesoma.s@northeastern.edu
 - Sahithi Sarabu | NUID:001067925 | sarabu.s@northeastern.edu
# Infrastructure as Code - RDS, Kubernetes cluster and VPC peering
## Prerequirements
- Configure AWS cli (aws account set-up with proper profile credentials/region needed).
- Install Ansible
- export aws profile ( export AWS_PROFILE=prod )
## Launch kubernetes cluster, setup networking components such as VPC, subnets, route table, internet gateway, security groups(allow kubernetes cluster CIDR), launch RDS instance using those created networking components and establish VPC peering between Kubernetes cluster VPC and RDS VPC using following command:
```shell
ansible-playbook playbooks/setup.yml --extra-vars "aws_profile=profilename aws_region=regionname name=clustername node_size=t2.micro master_size=t2.micro node_count=3 ssh_key=yoursshkey.pub k8s_version=1.16.15 k8s_vpc_name=k8svpcname"
```
>**NOTE**: Verify k8s cluster and RDS instance launched successfully and established VPC peering between them. Also verify kubernetes cluster route table has entry for RDS instance( RDS CIDR ) and RDS instance route table has entry for Kubernetes cluster( kubernetes CIDR ).

## Teardown RDS instance and its networking components, VPC peering and k8s cluster using following command:
```shell
ansible-playbook playbooks/delete.yml --extra-vars "aws_region=regionname name=clustername s3=bucketname aws_profile=profilename k8s_vpc_name=k8svpcname"
```
>**NOTE**: Verify RDS instance and its networking compenents are deleted, VPC peering is deleted and kubernets cluster is terminated using aws console
# Infrastructure as Code - Networking components, EC2, Jenkins
- Configure AWS cli (aws account set-up with proper profile credentials/region needed).
- Install Ansible
## Allocate Elastic IP
- Manually Allocate an Eastic IP address for Jenkins instance and tag it appropriately using AWS console.
- export allocated Elastic IP ( export IP=x.x.x.x )
- update /etc/ansible/hosts with allocated elastic IP under [webservers] as shown below
```txt
[webservers]
x.x.x.x ansible_user=ubuntu
```
## Setup networking components and launch EC2 instance as well as install java, jenkins, nginx, certbot and to use installed Certbot to generate certificates using following command:
```shell
ansible-playbook playbooks/setup.yml --extra-vars "aws_region=regionname sub_domain=domainname ec2_keypair=awskeypair email=useremailID ec2_image=ubuntuimage" -i hostfile
```
>**NOTE**: Verify all the required networking components are created and EC2 instance is up and running using aws console

>**NOTE**: Verify java, jenkins, nginx, certbot are installed on launched EC2 instance and able to get SSL certificate from Let's Encrypt.
And also Verify that Jenkins is running behind Nginx and is accessible over HTTPS.

## Teardown Network components and EC2 instance using following command:
```shell
ansible-playbook playbooks/teardown.yml --extra-vars "aws_region=regionname key=app value=jenkins"
```
>**NOTE**: Verify all the networking components are deleted and EC2 instance is terminated using aws console

