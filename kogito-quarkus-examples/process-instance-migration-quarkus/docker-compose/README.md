# Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the following services:
- Postgresql
- PgAdmin
- Kogito Process Instance Migration Service (Only available if the example has been compiled using the `container` mvn profile eg: ```mvn clean package -Pcontainer```)

The docker compose template provides three profiles to enable starting only the set of services you want to use. The profiles are:
- **infra**: Starts only the minimal infrastructure to run the example (Postgresql, pgadmin)
- **example**: Starts the services in *infra* profile and the Kogito Example Service. Requires the example to be compiled using the `container` mvn profile eg: ```mvn clean package -Pcontainer```.

> NOTE: In order to use it, please ensure you have Docker Compose installed on your machine, otherwise follow the instructions available
in [here](https://docs.docker.com/compose/install/).

## Starting the services

Use the `startServices.sh` passing the docker profile you want to use as an argument. If no profile is provided the script will default to **full**.

Eg:
```shell
sh startServices.sh example
```

Once the services are started (depending on the profile), the following ports will be assigned on your local machine:
- Postgresql: 5432
- PgAdmin: 8055
- Kogito Process Instance Migration Service: 8080

## Stopping and removing volume data

To stop all services, simply run:

```shell
docker compose stop
```
or 

```shell
docker compose down 
```
to stop the services and remove the containers.

For more details please check the Docker Compose documentation.

```shell
docker-compose --help
```
