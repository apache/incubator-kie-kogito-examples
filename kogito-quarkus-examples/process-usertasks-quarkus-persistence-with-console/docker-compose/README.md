## Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the following services:
- Postgresql / Infinispan /mongoDB
- (PgAdmin, only for postgres deployment) 
- Kafka
- Kogito Data Index
- Kogito Jobs
- Kogito Management Console
- Kogito Task Console
- Keycloak
- Kogito Runtime service example

In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions available
 in [here](https://docs.docker.com/compose/install/).
 
### Starting required services

 Once all services bootstrap, the following ports will be assigned on your local machine:
  - PostgresQL: 5432  or Infinispan: 11222 or MongoDB: 27017
  - PgAdmin: 8055 (only for postgres deployment) 
  - Kafka: 9092
  - Data Index: 8180
  - Jobs: 8580    
  - Management Console: 8280
  - Task Console: 8380
  - Keycloak: 8480

### Postgresql deployment:

####Start services

docker-compose -f docker-compose-postgresql.yml up  

#### Stopping and removing volume data
  
To stop all services, simply run:

docker-compose -f docker-compose-postgresql.yml stop

It is also recomended to remove any of stopped containers by running:

docker-compose -f docker-compose-postgresql.yml rm


### MongoDB deployment:

####Start services

docker-compose -f docker-compose-mongodb.yml up

#### Stopping and removing volume data

To stop all services, simply run:

docker-compose -f docker-compose-mongodb.yml stop

It is also recomended to remove any of stopped containers by running:

docker-compose -f docker-compose-mongodb.yml rm

### Infinispan deployment:

docker-compose -f docker-compose-infinispan.yml up

#### Stopping and removing volume data

To stop all services, simply run:

docker-compose -f docker-compose-infinispan.yml stop

It is also recomended to remove any of stopped containers by running:

docker-compose -f docker-compose-infinispan.yml rm


NOTE: All the running containers can be stopped running `docker stop  $(docker ps -a -q)`

NOTE: All the running containers can be removed running `docker rm  $(docker ps -a -q)`

For more details please check the Docker Compose documentation.

    docker-compose --help
