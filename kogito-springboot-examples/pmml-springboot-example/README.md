# PMML + Spring Boot example

## Description

A simple PMML service.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.9+ installed

### Compile and Run

```sh
mvn clean compile spring-boot:run
```

### Package and Run

```sh
mvn clean package
java -jar ./target/pmml-springboot-example.jar
```

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
