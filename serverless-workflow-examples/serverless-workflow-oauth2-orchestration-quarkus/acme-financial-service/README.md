# SonataFlow - Acme Financial Service

## Description

This project contains a simple quarkus application that implements the `Acme Financial Service` referred in the SonataFlow guide: [Orchestration of third-party services using OAuth 2.0 authentication](https://sonataflow.org/serverlessworkflow/latest/security/orchestrating-third-party-services-with-oauth2.html)

## Infrastructure requirements

The same infrastructure requirements as described in [Currency Exchange Workflow](../currency-exchange-workflow/README.md#infrastructure-requirements).

## Installing and Running

### Prerequisites

The same prerequisites as described in [Currency Exchange Workflow](../currency-exchange-workflow/README.md#prerequisites).

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
./target/acme-financial-service-{version}-runner
```

### Submit a request

Use the following curl command if you want to manually resolve a pending query:
```sh
curl -X 'GET' \
  'http://localhost:8483/financial-service/exchange-rate?currencyFrom=EUR&currencyTo=USD&exchangeDate=2022-06-10' \
  -H 'accept: application/json'
``` 

### Swagger UI

The swagger is also available in the following url: http://localhost:8483/q/swagger-ui
