# Kogito Serverless Workflow - Query Answer Service Example

## Description

This project contains a simple [serverless workflow](src/main/resources/qaservice.sw.json) and some auxiliary resources that implement the **Query and Answer Service** 
described in the [serverless-workflow-qas-service-showcase/README.md](../README.md). Please read it before to continue.

The service is described using JSON format as defined in the
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

## Infrastructure requirements

### Kafka

This quickstart requires an Apache Kafka to be available and, by default, expects it to be on the default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

The topic "query_response_events" is used to publish and consume the events.

Optionally and for convenience, a docker-compose [configuration file](../docker-compose/docker-compose.yml) is
provided in the path [../docker-compose](../docker-compose), where you can just run the command from there:

```sh
docker-compose up
```  

In this way, a container for Kafka will be started on port 9092.

### PostgreSQL

Alternatively, you can run this example using persistence with a PostgreSQL server.

Configuration for setting up the connection can be found in [applications.properties](src/main/resources/application.properties) file, which
follows the Quarkus JDBC settings, for more information please check [JDBC Configuration Reference](https://quarkus.io/guides/datasource#jdbc-configuration).

Optionally and for convenience, a docker-compose [configuration file](../docker-compose/docker-compose.yml) is
provided in the path [../docker-compose](../docker-compose), where you can just run the command from there:

```sh
docker-compose up
```  

In this way, a container for PostgreSQL will be started on port 5432.

## Installing and Running

### Prerequisites

You will need:
- Java 17+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.9.9+ installed

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
mvn clean package -Ppersistence 
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```sh
./target/query-answer-service-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be accessed by sending requests to the http://localhost:8080/qaservice url.

Use the following curl command to list all the active serverless workflow instances:

```sh
curl -X 'GET' 'http://localhost:8080/qaservice' -H 'accept: application/json'
``` 

Use the following curl command to create a new serverless workflow instance:

```sh
curl -X 'POST' 'http://localhost:8080/qaservice' -H 'accept: application/json' -H 'Content-Type: application/json' \
-d '{ "query" : "the text for my query" }'
```

Use the following curl command to list the knowledge database:
```sh
curl -X 'GET' 'http://localhost:8080/queries' -H 'accept: application/json'
``` 

### Swagger UI

The swagger is also available in the following url: http://localhost:8080/q/swagger-ui
