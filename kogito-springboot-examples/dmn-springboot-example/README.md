# DMN + Spring Boot example

## Description

A simple DMN service to evaluate a traffic violation and a model with input constraints.

The org.kie.dmn.runtime.typecheck=true property is used to enable type and value check.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

### Compile and Run

```sh
mvn clean compile spring-boot:run
```

### Package and Run

```sh
mvn clean package
java -jar ./target/dmn-springboot-example.jar
```

## Test DMN Model using Maven

Validate the functionality of DMN models before deploying them into a production environment by defining test scenarios in Test Scenario Editor. 

To define test scenarios you need to create a .scesim file inside your project and link it to the DMN model you want to be tested. Run all Test Scenarios, executing:

```sh
mvn clean test
```
See results in surefire test report `target/surefire-reports` 

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

### POST /AllowedValuesChecksInsideCollection

Valid interests for the model are: Golf, Computer, Hockey, Jogging

Given valid input:

```json
{
 "p1": {
  "Name": "Joe",
  "Interests": [
   "Golf"
  ]
 }
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"p1": {"Name":"Joe","Interests":["Golf"]}}' http://localhost:8080/AllowedValuesChecksInsideCollection
```

As response, interests information is returned.

Example response:
```json
{
 "p1": {
  "Interests": [
   "Golf"
  ],
  "Name": "Joe"
 },
 "MyDecision": "The Person Joe likes 1 thing(s)."
}
```

With invalid value

```json
{
 "p1": {
  "Name": "Joe",
  "Interests": [
   "Dancing"
  ]
 }
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"p1": {"Name":"Joe","Interests":["Dancing"]}}' http://localhost:8080/AllowedValuesChecksInsideCollection
```

As response, error information is returned.

Example response:
```json
{
 "namespace": "http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442",
 "modelName": "AllowedValuesChecksInsideCollection",
 "dmnContext": {
  "p1": {
   "Interests": [
    "Dancing"
   ],
   "Name": "Joe"
  }
 },
 "messages": [
  {
   "severity": "ERROR",
   "message": "Error while evaluating node 'MyDecision' for dependency 'p1': the dependency value '{Interests=[Dancing], Name=Joe}' is not allowed by the declared type (DMNType{ http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442 : Person })",
   "messageType": "ERROR_EVAL_NODE",
   "sourceId": "_27453770-68e3-48da-8605-d33a653c09ef",
   "level": "ERROR"
  }
 ],
 "decisionResults": [
  {
   "decisionId": "_ed3b9794-9306-4b6a-b4f9-5486be3c5515",
   "decisionName": "MyDecision",
   "result": null,
   "messages": [
    {
     "severity": "ERROR",
     "message": "Error while evaluating node 'MyDecision' for dependency 'p1': the dependency value '{Interests=[Dancing], Name=Joe}' is not allowed by the declared type (DMNType{ http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442 : Person })",
     "messageType": "ERROR_EVAL_NODE",
     "sourceId": "_27453770-68e3-48da-8605-d33a653c09ef",
     "level": "ERROR"
    }
   ],
   "evaluationStatus": "SKIPPED"
  }
 ]
}
```

## Developer notes

In order to have the DMN generated resources properly scanned by Spring Boot, please ensure the DMN model namespaces
 is included in the String application configuration.

The generated classes must be included in the annotation definitions of the main `Application` class:

```
@SpringBootApplication(scanBasePackages={"org.kie.kogito.**", "org.kie.kogito.app.**", "http*"})
public class KogitoSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(KogitoSpringbootApplication.class, args);
    }
}
```

