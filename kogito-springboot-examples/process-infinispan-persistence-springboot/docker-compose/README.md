## Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the following services:
- Infinispan
- Kafka
- Kogito Data Index
- Kogito Management Console
- Kogito Task Console
- Keycloak
- process-usertasks-springboot-with-console

In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions available
 in [here](https://docs.docker.com/compose/install/).
 
### Starting required services

  Before you execute the **Hiring** example, start all the services by following these steps:

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
  - Data Index: 8180
  - Management Console: 8280
  - Task Console: 8380
  - Keycloak: 8480
  - process-usertasks-springboot-with-console: 8080

> **_NOTE:_** If you don't want to run the example inside docker compose. You can stop the container by running below commands.

    docker stop process-usertasks-springboot-with-console

### Stopping and removing volume data
  
  To stop all services, simply run:

    docker-compose stop

  It is also recommended to remove any of stopped containers by running:
  
    docker-compose rm
    
  For more details please check the Docker Compose documentation.
  
    docker-compose --help
