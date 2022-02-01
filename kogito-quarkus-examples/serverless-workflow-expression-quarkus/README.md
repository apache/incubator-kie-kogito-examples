# Kogito Serverless Workflow - Expression Example

## Description

This example contains a simple workflow service that illustrate JQ expression usage. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).
The service accepts a number and calculate the square using JQ multiplication operator. 


## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.1+ installed

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


In Quarkus you should see the log message printed:

```text
4
```