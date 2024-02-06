# Kogito Serverless Workflow - Compensation Example

## Description

This example contains a simple workflow service that illustrate compensation handling. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).
This is simple workflow that expects a boolean `shouldCompensate` to indicate if compensation segment (which is composed by two `inject states`) should be executed or not.
The process result is a boolean field `compensated` which value should match `shouldCompensate`. 

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed

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
./target/serverless-workflow-compensation-quarkus-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/compensation'
with following content 

```json
{
  "shouldCompensate": true
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"shouldCompensate": true}' http://localhost:8080/compensation
```

Should return something like this ("id" will change)

```json
{
    "id": "b1e8ce8d-2fc5-4d39-b3b3-6f7dddbb1515",
    "workflowdata": {
        "shouldCompensate": true,
        "compensated": "true"
        "compensating_more": "Real Betis Balompie"
    }
}
```

If you would like to check output when there is no compensation

```json
{
  "shouldCompensate": false
}
```

Complete curl command can be found below:

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"shouldCompensate": false}' http://localhost:8080/compensation
```

Should return something like this ("id" will change)

```json
{
    "id": "c106c3f9-8a21-44c0-83df-1191b6a04672",
    "workflowdata": {
        "shouldCompensate": false,
        "compensated": false
    }
}
```
