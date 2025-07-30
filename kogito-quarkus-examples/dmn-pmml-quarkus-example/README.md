# DMN + PMML +  Quarkus example

## Description

A simple DMN + PMML service.

Demonstrates DMN on Kogito capabilities, including REST interface code generation.

## Installing and Running

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.9+ installed

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

