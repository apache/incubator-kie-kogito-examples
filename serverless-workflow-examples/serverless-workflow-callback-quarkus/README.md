# Kogito Serverless Workflow - Callback Example

## Description

This example contains a simple workflow service that illustrate callback state usage. 
A callback is a state that invokes an action and wait for an event (event that will be eventually fired by the external service notified by the action), so this example needs an event broker.
This example consist of a callback state that waits for an event arriving on wait channel. Its action publish an event on resume channel. The event published on resume channnel is modified and republished into the wait channel by `PrintService`, which simulates an external service. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

## Infrastructure requirements

### Kafka

This quickstart requires an Apache Kafka to be available and by default expects it to be on default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

To publish and consume the event, topic "move" is used. 

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:

```sh
docker-compose up
```

In this way a container for Kafka will be started on port 9092.

### PostgreSQL

This example also requires persistence with a PostgreSQL server.

Configuration for setting up the connection can be found in [applications.properties](src/main/resources/application.properties) file, which
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
  - Maven 3.9.6+ installed
  - Docker and Docker Compose to run the required example infrastructure.

When using native image compilation, you will also need: 
    - GraalVM 20.3+ installed
    - Environment variable GRAALVM_HOME set accordingly
    - GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
    - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

NOTE: Quarkus provides a way of creating a native Linux executable without GraalVM installed, leveraging a container runtime such as Docker or Podman. More details in  https://quarkus.io/guides/building-native-image#container-runtime 

### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```

### Start infrastructure services

You should start all the services before you execute any of the **Callback** example, to do that please execute:

```sh
mvn clean package -Pcontainer
```

For Linux and MacOS:

1. Open a Terminal
2. Go to docker-compose folder
3. Run the ```startServices.sh``` script

```bash
cd docker-compose && sh ./startServices.sh
```

Tip: If you get permission denied while creating the postgres container, consider to use SELinux context.
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
- Data Index: 8180
- PgAdmin: 8055
- sw-callback-service :8080

> **_NOTE:_**  This step requires the project to be compiled, please consider running a ```mvn clean package -Dcontainer``` command on the project root before running the ```startServices.sh``` script for the first time or any time you modify the project.

Once started you can simply stop all services by executing the ```docker-compose -f docker-compose.yml stop```.

All created containers can be removed by executing the ```docker-compose -f docker-compose.yml rm```.


### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```sh
./target/serverless-workflow-callback-quarkus-{version}-runner
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

