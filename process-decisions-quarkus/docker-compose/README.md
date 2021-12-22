## Kogito and Infrastructure services

To allow a quick setup of all the required services to run this demo, we provide a Docker Compose template that starts the following services:
- Infinispan
- Kafka
- Kogito Data Index

In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions available
 in [here](https://docs.docker.com/compose/install/).
 
### Starting required services

  Before you execute the **Hiring** example, start all the services by following these steps:

  For Linux and MacOS:

    ./startServices.sh

  For Windows:
   
  1. Create a .env file with the content containing the version of the Kogito images you would like to run, 
  for example:

    KOGITO_VERSION=1.0.0

  2. Execute the following command. 

    docker-compose -p kogito-services up -d

  Once all services bootstrap, the following ports will be assigned on your local machine:
  - Infinispan: 11222
  - Kafka: 9092
  - Data Index: 8180

### Stopping and removing volume data
  
  To stop all services, simply run:

    docker-compose stop

  It is also recommended to remove any of stopped containers by running:
  
    docker-compose rm
    
  For more details please check the Docker Compose documentation.
  
    docker-compose --help
