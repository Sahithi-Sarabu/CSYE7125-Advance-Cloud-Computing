# webapp
## API
[API](https://app.swaggerhub.com/apis-docs/csye7125/fall-2020/assignment-05#/authenticated)
## Team Members
 - Leming Li | NUID:001054503 | li.lem@northeastern.edu
 - Sahana Kannahallimole Somanna | NUID:001086735 | kannahallimolesoma.s@northeastern.edu
 - Sahithi Sarabu | NUID:001067925 | sarabu.s@northeastern.edu
## Continuous Integration
### Instance set up
  - Once the jenkins instance is up make sure you have docker permissions by running ```docker run hello-world``` SSHing into the instance
  - If you face any permission issues run ```sudo chmod 666 /var/run/docker.sock```
  - Generate a new ssh key ```ssh-keygen```
### Configure GitHub
  - Add the public key generated in the SSH & GPG keys of Github
  - Add a webhook in the webapp repository with ```Payload URL as https://your_jenkins_subdomain/github-webhook/``` and ```Content type as application/json```
  - Leave the rest of the options as default
### Set up Jenkins Job
  - Navigate to Manage Jenkins > Manage Plugins > Available and search for ```docker```
  - Select ```Cloudbees Docker Build and Publish``` and  ```Docker Pipeline``` plugins and click ```Install without restart```
  - Again Navigate to Manage Jenkins > Manage Credentials > Jenkins > Global Credentials > Add Credentials
  - Select ```Kind as SSH Username with private key``` and ID and username of your choice. For Private Key select```Enter directly``` and ```Add``` Copy the private key generated and click OK
  - Add another credentials with 
    - ```Kind as Username with password``` 
    - ```Username as your_docker_profile_username```
    - ```Password as your_docker_Account_password```
    - ```ID as docker-cred```
  - Navigate to Manage Jenkins > Configure Systems > Global Properties and select ```Environment Variables``` and click add to add ```DOCKER_ID as key``` and ```your_docker_repository as value```
  - From the Jenkins main page Select ```New Item``` and give the Name of your choice and select ```Pipeline``` and click ok
  - Scroll to ```Build Triggers``` and check ```GitHub hook trigger for GITScm polling```
  - Under Pipeline select ```Pipeline Script from SCM```, ```Git as SCM``` and provide the path & credentials to the repository
  - Under ```Branch Specifier as */* for testing and */main for demo```  and click SAVE
## Running the webapp locally
### Kafka
#### Start The Kafka Environment
>**NOTE**: Your local environment must have Java 8+ installed.

```shell script
# Go to the kafka folder you downloaded
cd kafka_2.13-2.6.0
```
Run the following commands in order to start all services in the correct order:
```shell script
# Start the ZooKeeper service
# Note: Soon, ZooKeeper will no longer be required by Apache Kafka.
$ bin/zookeeper-server-start.sh config/zookeeper.properties
```
Open another terminal session and run:
```shell script
# Start the Kafka broker service
$ bin/kafka-server-start.sh config/server.properties
```
Read the events
```shell script
$ bin/kafka-console-consumer.sh --topic weather --from-beginning --bootstrap-server localhost:9092
```
#### Terminate The Kafka Environment
- Stop the producer and consumer clients with `Ctrl-C`, if you haven't done so already.
- Stop the Kafka broker with `Ctrl-C`.
- Lastly, stop the ZooKeeper server with `Ctrl-C`.

If you also want to delete any data of your local Kafka environment including any events you have created along the way, run the command:
```shell script
$ rm -rf /tmp/kafka-logs /tmp/zookeeper
```

### Docker
#### Pulling Images
  - ```docker pull your_docker_repository:latest```
  - ```docker pull mysql``` to pull images
#### Creating Containers
  - ```docker run -d -p 8080:8080 --name webapp-container --link mysql-container sahithis/cloud-backend```
#### To check status of containers (Optional)
  - ```docker container logs mysql-container```
  - ```docker container logs webapp-container```
Now you are all set to run your application on Postman/Browser 
#### To stop the containers
  - ```docker stop mysql-container```
  - ```docker stop webapp-container```