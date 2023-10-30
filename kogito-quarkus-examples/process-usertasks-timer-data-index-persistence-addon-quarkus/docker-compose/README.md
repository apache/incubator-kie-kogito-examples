# Kogito and Infrastructure services

To allow a quick setup of all services required to run this demo, we provide a docker compose template that starts the following services:
- Postgresql
- PgAdmin
- Kogito Data Index
- Kogito Jobs Service
- Kogito Example Service (Only available if the example has been compiled using the `container` mvn profile eg: ```mvn cleanp package -Dcontainer```)
- Kogito Management Console
- Kogito Task Console
- Keycloak

The docker compose template provides three profiles to enable starting only the set of services you want to use. The profiles are:
- **infra**: Starts only the minimal infrastructure to run the example (Postgresql, pgadmin, Kogito Data Index & Jobs Service)
- **example**: Starts the services in *infra* profile and the Kogito Example Service. Requires the example to be compiled using the `container` mvn profile eg: ```mvn cleanp package -Dcontainer```.
- **full** (default): includes all the above and also starts the **Management Console**, **Task Console** and a **Keycloak** to handle the consoles authentication. Requires the example to be compiled using the `container` mvn profile eg: ```mvn cleanp package -Dcontainer```.

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
- Kogito Data Index: 8180
- Kogito Jobs: 8580
- Kogito Example Service: 8080
- Kogito Management Console: 8280
- Kogito Task Console: 8380
- Keycloak: 8480

## Stopping and removing volume data

To stop all services, simply run:

```shell
docker compose stop
```
or 

```shell
docker compose down 
```
to stop the services and remove the containers
docker-compose -f docker-compose-postgresql.yml stop

For more details please check the Docker Compose documentation.

```shell
docker-compose --help
```
