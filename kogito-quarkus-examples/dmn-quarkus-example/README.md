# DMN + Quarkus example

## Description

A simple DMN service to evaluate a traffic violation and a model with input constraints.

The org.kie.dmn.runtime.typecheck=true property is used to enable type and value check.

Demonstrates DMN on Kogito capabilities, including REST interface code generation.

## Installing and Running

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed

When using native image compilation, you will also need:
  - [GraalVM 19.3.1](https://github.com/oracle/graal/releases/tag/vm-19.3.1) installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on Windows

```
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/dmn-quarkus-example-runner
```

Note: This does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon.

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

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
    "Driver":{"Points":2},
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
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"Driver\":{\"Points\":2},\"Violation\":{\"Type\":\"speed\",\"Actual Speed\":120,\"Speed Limit\":100}}" http://localhost:8080/Traffic%20Violation
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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d 'p1": {"Name":"Joe","Interests":["Golf"]}' http://localhost:8080/AllowedValuesChecksInsideCollection
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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d 'p1": {"Name":"Joe","Interests":["Dancing"]}' http://localhost:8080/AllowedValuesChecksInsideCollection
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
