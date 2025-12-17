# Kogito Serverless Workflow - Expression Example

## Description

This example contains a simple workflow service that illustrate JQ expression usage. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).
The service accepts an array of complex numbers (x being the real coordinate and y the imaginary one) and return the square of the max real coordinate. 


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

or on Windows

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
./target/serverless-workflow-expression-quarkus-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/expression'
with following content 

```json
{
    "workflowdata": {
        "numbers": [
            {
                "x": 2,
                "y": 1
            },
            {
                "x": 4,
                "y": 3
            }
        ]
    }
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata":{"numbers":[{"x":2, "y": 1},{"x":4, "y": 3}]}}' http://localhost:8080/expression
```



And the returned data will be something similar to 

```json
{
    "id": "9f30a25e-61d4-4e80-bc7c-eb04db51564c",
    "workflowdata": {
        "result": 2.0
    }
}
```

