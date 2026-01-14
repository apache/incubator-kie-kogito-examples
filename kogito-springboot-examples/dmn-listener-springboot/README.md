# DMN + Spring Boot example with listeners

## Description

A simple DMN service to evaluate a traffic violation, with the addition of some DMN listeners.

Demonstrates Kogito capability to automatically inject custom listeners in the DMN runtime without the need of writing a single line of Java code for the wiring itself.

Listener injection is _optional_. If you don't need it, just ignore it.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed

### Compile and Run

```sh
mvn clean compile spring-boot:run
```

### Package and Run

```sh
mvn clean package
java -jar ./target/dmn-listener-springboot.jar
```

## Listener injection

Kogito allows you to inject custom instances of `DMNRuntimeEventListener` if you need to attach custom logic to every DMN model evaluation.

There are two ways to do this:
* Create one or more standard listener classes and annotate them with `Component` (the quickest way to inject a single listener). Demonstrated in `ExampleDMNRuntimeEventListener` class.
* Create one or more instances of `DecisionEventListenerConfig` (returning a list of listeners each) and annotate them with `Component`. Demonstrated in `ExampleDecisionEventListenerConfig` class.

All the listeners instantiated with both methods will be injected during the application startup phase.

## Example Usage

Once the service is up and running, you can use the following example to interact with the service.

### POST /Traffic Violation

Returns penalty information from the given inputs -- driver and violation:
Given inputs:

```json
{
    "Driver":{ "Points":2 },
    "Violation":{
        "Type":"speed",
        "Actual Speed":120,
        "Speed Limit":100
    }
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"Driver":{"Points":2},"Violation":{"Type":"speed","Actual Speed":120,"Speed Limit":100}}' http://localhost:8080/Traffic%20Violation
```

As response, penalty information is returned.

Example response:
```json
{
  "Violation":{
    "Type":"speed",
    "Speed Limit":100,
    "Actual Speed":120
  },
  "Driver":{
    "Points":2
  },
  "Fine":{
    "Points":3,
    "Amount":500
  },
  "Should the driver be suspended?":"No"
}
```
