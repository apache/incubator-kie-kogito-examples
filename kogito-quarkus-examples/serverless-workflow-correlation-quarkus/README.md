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

Alternatively, you can run this example using persistence with a PostgreSQL server.

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
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.1+ installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```

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

### Compile and Run in JVM mode using PostgreSQL persistence

To enable persistence, please append `-Ppersistence` to your Maven command.
That will ensure the correct dependencies are in place, and automatically set the required properties to connect
with the PostgreSQL instance from the provided docker compose.

```sh
mvn clean package -Peristence 
```

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

