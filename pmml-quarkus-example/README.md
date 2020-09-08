# PMML + Quarkus example

## Description

A simple PMML service

Demonstrates PMML on Kogito capabilities, including REST interface code generation.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

### Compile and Run in Local Dev Mode

```
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/pmml-quarkus-example-runner.jar
```

or on Windows

```
mvn clean package
java -jar target\pmml-quarkus-example-runner.jar
```

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.



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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"fld1":3.0, "fld2":2.0, "fld3":"y"}' http://localhost:8080/LinReg
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"fld1":3.0, "fld2":2.0, "fld3":"y"}" http://localhost:8080/LinReg
```

Example response:

```json
{
  "correlationId":null,
  "segmentationId":null,
  "segmentId":null,
  "segmentIndex":0,
  "resultCode":"OK",
  "resultObjectName":"fld4",
  "resultVariables":
  {
    "fld4":52.5
  }
}
```

### POST /Tree

Given inputs:

```json
{
  "temperature":30.0, 
  "humidity": 10.0 
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"temperature":30.0, "humidity":10.0}' http://localhost:8080/SampleMine
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"temperature":30.0, "humidity":10.0}" http://localhost:8080/SampleMine
```

Example response:

```json

{ 
  "correlationId":null,
  "segmentationId":null,
  "segmentId":null,
  "segmentIndex":0, 
  "resultCode":"OK",
  "resultObjectName":"decision",
  "resultVariables": {
          "decision":"sunglasses",
          "weatherdecision":"sunglasses" 
                      }
}
```

### POST /Scorecard

Given inputs:

```json
{
  "input1":5.0, 
  "input2":-10.0
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"input1":5.0, "input2":-10.0}' http://localhost:8080/SimpleScorecard
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"input1":5.0, "input2":-10.0}" http://localhost:8080/SimpleScorecard
```

Example response:

```json
{ 
  "correlationId":null,
  "segmentationId":null,
  "segmentId":null,
  "segmentIndex":0, 
  "resultCode":"OK",
  "resultObjectName":"score",
  "resultVariables": {
          "score":-15.0,
          "Score":-15.0,
          "Reason Code 1":"Input1ReasonCode",
          "Reason Code 2":"Input2ReasonCode"
          }
}
```

### POST /MiningModel

Given inputs:

```json
{"residenceState":"AP",
                "validLicense":true,
                "occupation":"ASTRONAUT",
                "categoricalY":"classA",
                "categoricalX":"red",
                "variable":6.6,
                "age":25.0
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"residenceState":"AP", "validLicense":true, "occupation":"ASTRONAUT", "categoricalY":"classA", "categoricalX":"red", "variable":6.6, "age":25.0}' http://localhost:8080/PredicatesMining
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"residenceState":"AP", "validLicense":true, "occupation":"ASTRONAUT", "categoricalY":"classA", "categoricalX":"red", "variable":6.6, "age":25.0}" http://localhost:8080/PredicatesMining
```

Example response:

```json
{
  "correlationId": null,
  "segmentationId": null,
  "segmentId": null,
  "segmentIndex": 0,
  "resultCode": "OK",
  "resultObjectName": "categoricalResult",
  "resultVariables": {
    "categoricalResult": 1.381666666666666
  }
}
```

## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
