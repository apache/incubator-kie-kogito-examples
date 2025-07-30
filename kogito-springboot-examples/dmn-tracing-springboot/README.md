# DMN Tracing Spring Boot example

## Description

A simple DMN service to evaluate a loan approval and generate tracing events that might be consumed by the Trusty service.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.9+ installed

### Configuration of the tracing addon

The default configuration pushes the tracing events to the kafka topic `kogito-tracing-decision` and the DMN models used by the kogito application to `kogito-tracing-model` under the group-id `kogito-runtimes`. 
Edit the `application.properties` file to change the configuration. The property names are the following: 

- `kogito.addon.tracing.decision.kafka.bootstrapAddress`: The address used in the initial connection to find a bootstrap server on the cluster of `n` brokers (no default value, this is mandatory).
- `kogito.addon.tracing.decision.kafka.topic.name` : The topic name for the decision tracing events (default `kogito-tracing-decision`).  
- `kogito.addon.tracing.decision.kafka.topic.partitions` : How many partitions to use for the decision tracing events topic (default `1`).
- `kogito.addon.tracing.decision.kafka.topic.replicationFactor` : The replication factor of the data for the decision tracing events topic (default `1`).
- `kogito.addon.tracing.decision.asyncEnabled`: Use an asynchronous callback with the results of the send (success or failure) instead of waiting for the `Future` to complete (default `true`).
- `kogito.addon.tracing.model.kafka.topic.name` : The topic name for the DMN models used by the kogito application (default `kogito-tracing-model`).

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

When the tracing addon is enabled, the tracing events are emitted and pushed to a Kafka broker. The [Trusty Service](https://github.com/apache/incubator-kie-kogito-apps/tree/main/trusty) can consume such events and store them on a storage. The Trusty Service exposes then some api to consume the information that has been collected.
