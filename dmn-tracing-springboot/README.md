# DMN Tracing Spring Boot example

## Description

A simple DMN service to evaluate a loan approval and generate tracing events that might be consumed by the Trusty service.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

### Compile and Run in Local Dev Mode

```
mvn clean compile spring-boot:run
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/dmn-tracing-springboot.jar
```

or on Windows

```
mvn clean package
java -jar target\dmn-tracing-springboot.jar
```

## Example Usage

Once the service is up and running, you can use the following example to interact with the service.

### POST /LoanEligibility

Returns penalty information from the given inputs -- driver and violation:

Given inputs:

```json
{
  "Bribe": 0,
  "Client": {
    "age": 0,
    "existing payments": 0,
    "salary": 0
  },
  "Loan": {
    "duration": 0,
    "installment": 0
  },
  "SupremeDirector": "yes"
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"Bribe": 0,"Client": {"age": 0,"existing payments": 0,"salary": 0},"Loan": {"duration": 0,"installment": 0},"SupremeDirector": "yes"}' http://localhost:8080/LoanEligibility
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"Bribe\": 0,\"Client\": {\"age\": 0,\"existing payments\": 0,\"salary\": 0},\"Loan\": {\"duration\": 0,\"installment\": 0},\"SupremeDirector\": \"yes\"}" http://localhost:8080/LoanEligibility
```

As response, penalty information is returned.

Example response:

```json
{
  "Eligibility": "No",
  "Judgement": null,
  "Loan": {
    "duration": 0,
    "installment": 0
  },
  "SupremeDirector": "yes",
  "Bribe": 0,
  "Client": {
    "existing payments": 0,
    "salary": 0,
    "age": 0
  },
  "Is Enough?": 0,
  "Decide": null
}
```

## Integration example with Trusty Service

When the tracing addon is enabled, the tracing events are emitted and pushed to a Kafka broker. The [Trusty Service](https://github.com/kiegroup/kogito-apps/tree/master/trusty) can consume such events and store them on a storage. The Trusty Service exposes then some api to consume the information that has been collected. 
A `docker-compose` example is provided in the current folder. In particular, when `docker-compose up` is run, a Kafka broker, an Infinispan container and the nightly build of the trusty service are deployed. 
Once the services are up and running, after a decision has been evaluated, you can access the trusty service swagger at `localhost:8081/swagger-ui/` and try to query what are the evaluations of the last day at `localhost:8081/v1/executions` for example.
