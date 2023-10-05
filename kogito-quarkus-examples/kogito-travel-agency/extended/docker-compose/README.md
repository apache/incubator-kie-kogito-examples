## Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the
following services:

- Infinispan
- Kafka
- Prometheus
- Grafana
- Keycloak
- Kogito Data Index
- Kogito Management Console

This setup ensures that all services are connected using the default configuration as well as provisioning the Travel
Agency dashboard to Grafana.

In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions
available
in [here](https://docs.docker.com/compose/install/).

### Starting required services

You should start all the services before you execute any of the Travel Agency applications, to do that please execute:

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
- Prometheus: 9090
- Grafana: 3000
- Keycloak: 8480
- Data Index: 8180
- Management Console: 8280

To access the Grafana dashboard, simply navigate to http://localhost:3000 and login using the default username 'admin'
and password 'admin'.
Prometheus will also be available on http://localhost:9090, no authentication is required.

### Stopping and removing volume data

To stop all services, simply run:

    docker-compose stop

It is also recommended to remove any of stopped containers by running:

    docker-compose rm

For more details please check the Docker Compose documentation.

    docker-compose --help
