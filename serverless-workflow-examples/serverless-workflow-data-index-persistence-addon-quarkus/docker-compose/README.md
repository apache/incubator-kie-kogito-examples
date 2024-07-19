## Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the following services:
- Postgresql
- PgAdmin
- Data Index

In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions available
in [here](https://docs.docker.com/compose/install/).

### Starting required services

Once all services bootstrap, the following ports will be assigned on your local machine:
- PostgresQL: 5432  
- PgAdmin: 8055 
- Data Index: 8180

### Postgresql deployment:

####Start services

./startServices.sh 

#### Stopping and removing volume data

To stop all services, simply run:

docker-compose stop

It is also recomended to remove any of stopped containers by running:

docker-compose rm

NOTE: All the running containers can be stopped running `docker stop  $(docker ps -a -q)`

NOTE: All the running containers can be removed running `docker rm  $(docker ps -a -q)`

For more details please check the Docker Compose documentation.

    docker-compose --help
