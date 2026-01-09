# Kogito Serverless Workflow - Data Index Example

## Description

This example contains a simple workflow service that demonstrates how to use Data Index Addon as part of the Kogito runtime. 
A callback is a state that invokes an action and waits for an event (an event that will be eventually fired by the external service notified by the action), so this example needs an event broker.
This example consists of a callback state that waits for an event arriving at the `wait` channel. Its action is to publish an event on the `resume` channel. The event published on the `resume` channel is modified and republished into the `wait` channel by `PrintService`, which simulates an external service. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

## Infrastructure requirements

### Kafka

This quickstart requires an Apache Kafka to be available and by default expects it to be on default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

To publish and consume the event, the topic `move` is used. 

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:

```sh
docker-compose up
```

In this way a container for Kafka will be started on port 9092.

### PostgreSQL

This example also requires persistence with a PostgreSQL server.

The configuration for setting up the connection can be found in [applications.properties](src/main/resources/application.properties) file, which
follows the Quarkus JDBC settings, for more information please check [JDBC Configuration Reference](https://quarkus.io/guides/datasource#jdbc-configuration).

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:

```sh
docker-compose up
```

In this way a container for PostgreSQL will be started on port 5432.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed
  - Docker and Docker Compose to run the required example infrastructure.

When using native image compilation, you will also need: 
    - GraalVM 22.2+ installed
    - Environment variable GRAALVM_HOME set accordingly
    - GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
    - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

NOTE: Quarkus provides a way of creating a native Linux executable without GraalVM installed, leveraging a container runtime such as Docker or Podman. More details in  https://quarkus.io/guides/building-native-image#container-runtime 

### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```
Here we can run the dev mode in two scenarios:
1. Starting dev mode with kogito-addons-quarkus-data-index. It means the Data Index functionality will be exposed as part of the runtime service, no specific service started for audit data:
```sh
mvn clean package quarkus:dev -Pdata-index-addon
```
NOTE: Data Index graphql UI will be available in http://localhost:8080/q/graphql-ui/

2. Starting dev mode with Data index as a Quarkus Dev service
```sh
mvn clean package quarkus:dev -Pdata-index-devservice
```

NOTE: Data Index graphql UI will be available in http://localhost:8180/q/graphql-ui/


### Start infrastructure services

You should start all the services before you execute any of the **Data Index** example. To do that please execute:

```sh
mvn clean package -Pcontainer,data-index-addon 
```

For Linux and MacOS:

1. Open a Terminal
2. Go to docker-compose folder
3. Run ```docker compose up```

```bash
cd docker-compose && docker compose up
```

TIP: If you get a `permission denied` error while creating the postgresql container, consider using SELinux context.
Update the following line:
```yaml
    - ./sql:/docker-entrypoint-initdb.d
```
to
```yaml
    - ./sql:/docker-entrypoint-initdb.d:Z
```

Once all services bootstrap, the following ports will be assigned on your local machine:

- PostgreSQL: 5432
- Kafka: 9092
- PgAdmin: 8055
- serverless-workflow-service: 8080

> **_NOTE:_**  This step requires the project to be compiled, please consider running a ```mvn clean package -P container,data-index-addon``` command on the project root before running the ```docker-compose up``` for the first time or any time you modify the project.

Once started you can simply stop all services by executing the ```docker-compose -f docker-compose.yml stop```.

All created containers can be removed by executing the ```docker-compose -f docker-compose.yml rm```.

Data Index GraphQL UI is available at http://localhost:8080/q/graphql-ui/

This example provides also the configuration needed to see the dataindex deployed as a standalone service following the steps:
- Execute

```sh
mvn clean package -Ddata-index-standalone
```

- Run docker-compose to start all the services:
 
For Linux and MacOS:

1. Open a Terminal
2. Go to docker-compose folder
3. Run ```./startServices.sh```

or 

```bash
cd docker compose && ./startServices.sh
```

Once all services bootstrap, the following ports will be assigned on your local machine:

- PostgreSQL: 5432
- Kafka: 9092
- PgAdmin: 8055
- DataIndex: 8180
- serverless-workflow-service: 8080

> **_NOTE:_**  This step requires the project to be compiled, please consider running a ```mvn clean package -Ddata-index-standalone``` command on the project root before running the ```./startServices.sh.``` for the first time or any time you modify the project.

Once started you can simply stop all services by executing the ```docker-compose -f docker-compose-with-data-index.yml stop```.

All created containers can be removed by executing the ```docker-compose -f docker-compose-with-data-index.yml rm```.

### Compile and Run in JVM mode

```sh
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

or on Windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Dnative
```
  
To run the generated native executable, generated in `target/`, execute

```sh
./target/serverless-workflow-data-index-quarkus-runner
```

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/callback

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/callback
```


After a while (note that to you need give time for event to be consumed)  you should see the log message printed in quarkus:

```text
 Workflow data {"move":"This is the initial data in the model and has been modified by the event publisher"}
```

### Query process details in Data Index

Data Index GraphQL UI is available at http://localhost:8180/graphiql/