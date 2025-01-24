## Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the following services:
- Postgresql
- (PgAdmin, only for postgres deployment)
- Kafka
- Kogito Data Index
- Kogito Jobs

In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions available
in [here](https://docs.docker.com/compose/install/).

### Starting required services

Once all services bootstrap, the following ports will be assigned on your local machine:
- PostgresQL: 5432
- PgAdmin: 8055 (only for postgres deployment)
- Kafka: 9092
- Data Index: 8180
- Jobs: 8580

### Postgresql deployment:

####Start services

   ./startServices.sh postgresql or just ./startServices.sh 


#### Stopping and removing volume data

To stop all services, simply run:

docker-compose -f docker-compose-postgresql.yml stop

It is also recomended to remove any of stopped containers by running:

docker-compose -f docker-compose-postgresql.yml rm
