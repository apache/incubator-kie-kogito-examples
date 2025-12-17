# Kogito Serverless Workflow - DMN Example

## Description

This example contains a simple workflow service that use DMN. 
The services are described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

The workflow expects as JSON input containing driver details and a traffic violation 
(see details in the [Submit a request](#Submit-a-request) section).

The workflow uses that input to execute a decision file which evaluates if the driver should be suspended or not. 

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```

### Compile and Run in JVM mode

```sh
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```sh
./target/serverless-workflow-dmn-quarkus-{version}-runner
```

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
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"Driver":{"Points":2},"Violation":{"Type":"speed","Actual Speed":120,"Speed Limit":100}}' http://localhost:8080/traffic-violation
```
or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"Driver\":{\"Points\":2},\"Violation\":{\"Type\":\"speed\",\"Actual Speed\":120,\"Speed Limit\":100}}" http://localhost:8080/traffic-violation
```

As response, penalty information is returned.

Example response:

```json
{"workflowdata":
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
}
```