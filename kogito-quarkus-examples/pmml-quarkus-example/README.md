# PMML + Quarkus example

## Description

A simple PMML service

Demonstrates PMML on Kogito capabilities, including REST interface code generation.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed
  
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
./target/pmml-quarkus-example-runner
```

Note: This does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon.

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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"fld1":3.0, "fld2":2.0, "fld3":"y"}' http://localhost:8080/Testregression/LinReg
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"fld1":3.0, "fld2":2.0, "fld3":"y"}" http://localhost:8080/Testregression/LinReg
```

Example response:

```json
{
  "fld4":52.5
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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"temperature":30.0, "humidity":10.0}' http://localhost:8080/Testtree/SampleMine
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"temperature":30.0, "humidity":10.0}" http://localhost:8080/Testtree/SampleMine
```

Example response:

```json
{
  "decision":"sunglasses"
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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"input1":5.0, "input2":-10.0}' http://localhost:8080/Testscorecard/SimpleScorecard
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"input1":5.0, "input2":-10.0}" http://localhost:8080/Testscorecard/SimpleScorecard
```

Example response:

```json
{
  "score":-15.0
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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"residenceState":"AP", "validLicense":true, "occupation":"ASTRONAUT", "categoricalY":"classA", "categoricalX":"red", "variable":6.6, "age":25.0}' http://localhost:8080/Testminingmodel/PredicatesMining
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{"residenceState":"AP", "validLicense":true, "occupation":"ASTRONAUT", "categoricalY":"classA", "categoricalX":"red", "variable":6.6, "age":25.0}" http://localhost:8080/Testminingmodel/PredicatesMining
```

Example response:

```json
{
  "categoricalResult":1.381666666666666
}
```
