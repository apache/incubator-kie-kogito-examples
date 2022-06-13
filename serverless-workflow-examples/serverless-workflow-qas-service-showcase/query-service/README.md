# Kogito Serverless Workflow - Query Service Example

## Description

This project contains a simple quarkus application that implements the external Query Service described in [serverless-workflow-qas-service-showcase/README.md](../README.md)
and where the queries are resolved. Please read it before to continue.

## Infrastructure requirements

The same infrastructure requirements as described in [Query Answer Service](../query-answer-service/README.md#infrastructure-requirements) apply for this service.

## Installing and Running

### Prerequisites

The same prerequisites as described in [Query Answer Service](../query-answer-service/README.md#prerequisites) apply for this service.

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
That will ensure the correct dependencies are in place and automatically set the required properties to connect
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
./target/query-service-{version}-runner
```

### Submit a request

Use the following curl command to list all the pending queries in the query service:

```sh
 curl -X 'GET'  'http://localhost:8283/query-service'  -H 'accept: application/json'
 ```

Use the following curl command if you want to manually resolve a pending query:
```sh
curl -X 'POST' \
  'http://localhost:8283/query-service/resolveQuery' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "processInstanceId": "A valid SW process instance id -> 355eebfb-2c88-4f4a-969d-290197ddfc80",
  "queryResponse": "The response to send"
}'
``` 

### Swagger UI

The swagger is also available in the following url: http://localhost:8283/q/swagger-ui
