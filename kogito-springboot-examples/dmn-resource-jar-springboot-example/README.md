# DMN + Spring Boot example with model included in different jar

## Description

A simple DMN service to evaluate a model (traffic violation) that is imported from a different jar.
It also features the usage of custom DMN profiles, imported from a dependency and declared inside the application.properties file.

Demonstrates DMN on Kogito capabilities, including REST interface code generation.

## Installing and Running

### Prerequisites

You will need:
- Java 17+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.9.11+ installed

### Compile and Run

```sh
mvn clean install
cd ./dmn-springboot-consumer-example
mvn spring-boot:run
```

### Package and Run

```sh
mvn clean install
cd ./dmn-springboot-consumer-example
java -jar ./target/dmn-springboot-consumer-example.jar
```

## Test DMN Model using Maven

Validate the functionality of DMN models before deploying them into a production environment by defining test scenarios in Test Scenario Editor. 

To define test scenarios you need to create a .scesim file inside your project and link it to the DMN model you want to be tested. Run all Test Scenarios, executing:

```sh
cd ./dmn-springboot-consumer-example
mvn clean test
```
See results in surefire test report `target/surefire-reports` 

(This requires a previous installation of `dmn-resource-jar`)

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

The difference from the [dmn-springboot-example](../dmn-springboot-example) is that, in the current one, the `Traffic Model.dml` is defined in a different jar.



