## Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the following services:
- Infinispan
- Kafka
- Keycloak
- Kogito Data Index
- Kogito Management Console
- Kogito Task Console

This setup ensures that all services are connected using the default configuration as well as provisioning the Travel Agency dashboard to Grafana.  

In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions available
 in [here](https://docs.docker.com/compose/install/).
 
### Starting required services

  Before you execute the **Approvals** example, start all the services by following these steps:

  For Linux and MacOS:

    ./startServices.sh

  For Windows:
   
  Create a .env file with the content containing the version of the Kogito images you would like to run, example:

    KOGITO_VERSION=1.0.0

  Then run

    docker-compose up

  Once all services bootstrap, the following ports will be assigned on your local machine:
  - Infinispan: 11222
  - Kafka: 9092
  - Keycloak: 8480
  - Data Index: 8180
  - Management Console: 8280
  - Task Console: 8380

### Stopping and removing volume data
  
  To stop all services, simply run:

    docker-compose stop
    
  It is also recommended to remove any of stopped containers by running:
  
    docker-compose rm
    
  For more details please check the Docker Compose documentation.
  
    docker-compose --help
