# Ansible Playbooks for AWS Infrastructure & Jenkins Setup
## Prerequirements
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
## Setup networking components such as VPC, subnets, route table, internet gateway, security groups and launch EC2 instance using following command:
```shell
ansible-playbook playbooks/jenkins_infra/setup.yml --extra-vars "aws_region=regionname sub_domain=domainname ec2_keypair=awskeypair email=useremailID ec2_image=ubuntuimage"
```
>**NOTE**: Verify all the required networking components are created and EC2 instance is up and running using aws console

## Install java, jenkins, nginx, certbot and to use installed Certbot to generate certificates using following command
```shell
ansible-playbook playbooks/jenkins.yml --extra-vars "sub_domain=domainname email=useremailID" -i hostfile
```
>**NOTE**: Verify java, jenkins, nginx, certbot are installed on launched EC2 instance and able to get SSL certificate from Let's Encrypt.
And also Verify that Jenkins is running behind Nginx and is accessible over HTTPS.

## Teardown Network components and EC2 instance using following command:
```shell
ansible-playbook playbooks/jenkins_infra/teardown.yml --extra-vars "aws_region=regionname key=app value=jenkins"
```
>**NOTE**: Verify all the networking components are deleted and EC2 instance is terminated using aws console
