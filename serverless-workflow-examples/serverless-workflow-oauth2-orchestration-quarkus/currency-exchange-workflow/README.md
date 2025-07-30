# SonataFlow - Currency Exchange Workflow

## Description

This project contains a [workflow](src/main/resources/currency-exchange-workflow.sw.json) and some auxiliary resources that implement the **Currency Exchange Workflow**
described in the SonataFlow guide: [Orchestration of third-party services using OAuth 2.0 authentication](https://sonataflow.org/serverlessworkflow/latest/security/orchestrating-third-party-services-with-oauth2.html).

The service is described using JSON format as defined in the
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification/tree/0.8.x).

## Infrastructure requirements

### Keycloak

This example requires a Keycloak server to be running, and expects it to be listening on the port 8281 and localhost.

* Run the Keycloak server

In a new terminal, go to the `serverless-workflow-oauth2-orchestration-quarkus/scripts` directory and execute:

```sh
$ cd serverless-workflow-oauth2-orchestration-quarkus/scripts

$ ./startKeycloak.sh
```

Alternatively, you can use docker-compose following this procedure:

```sh
$ cd serverless-workflow-oauth2-orchestration-quarkus/docker-compose

$ docker-compose up
```

In this way, a container for Keycloak will be started on port 8281.
You can navigate to this URL [Keycloak console](http://localhost:8281/auth) to check that the server is running. 

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

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```sh
./target/currency-exchange-workflow-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be accessed by sending requests to the http://localhost:8080/currency-exchange-workflow url.

Use the following curl command to create a new workflow instance and get the results:


```sh
curl -X 'POST' \
  'http://localhost:8080/currency_exchange_workflow' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
       "currencyFrom": "EUR",
       "currencyTo": "USD",
       "exchangeDate": "2022-06-10",
       "amount": 2.0
    }'
```
