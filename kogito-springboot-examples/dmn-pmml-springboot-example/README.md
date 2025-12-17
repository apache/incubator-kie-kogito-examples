# DMN + PMML + Spring Boot example

## Description

A simple DMN + PMML service.

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
java -jar ./target/dmn-pmml-springboot-example.jar
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

### POST /Regression

Given inputs:

```json
{
  "fld1":3.0, 
  "fld2":2.0, 
  "fld3":"y"
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"fld1":3.0, "fld2":2.0, "fld3":"y"}' http://localhost:8080/TestRegressionDMN
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"fld1":3.0, "fld2":2.0, "fld3":"y"}" http://localhost:8080/TestRegressionDMN
```

Example response:

```json
{
  "RegressionModelBKM":"function RegressionModelBKM( fld1, fld2, fld3 )",
  "fld3":"y",
  "fld2":2.0,
  "fld1":3.0,
  "Decision":52.5
}
```

### POST /Tree

Given inputs:

```json
{
  "temperature":30, 
  "humidity": 10 
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"temperature":30, "humidity":10}' http://localhost:8080/TestTreeDMN
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"temperature":30, "humidity":10}" http://localhost:8080/TestTreeDMN
```

Example response:

```json
{  
  "TestTreeBKM":"function TestTreeBKM( humidity, temperature )",
  "temperature":30,
  "humidity":10,
  "Decision":"sunglasses"
}
```
