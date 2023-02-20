# Kogito Serverless Workflow - ForEach Example

## Description

This example contains a simple workflow service that illustrate foreach state usage. 
`ForEach` is a state that invokes an action over any item of an input collection.
This example consist of just one `foreach` state. A list of `int` is passed and every item increase its value one unit. Another action prints the updated value. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).


## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```text
mvn clean package quarkus:dev    
```

### Compile and Run in JVM mode

```text
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```text
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```text
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```text
./target/serverless-workflow-foeach-quarkus-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/foreach

Complete curl command can be found below:

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"input": [1,2,3]}' http://localhost:8080/foreach
```


once completed, it should return:

```json
{ 
  "workflowdata": {
        "output": [
            2,
            3,
            4
        ]
    }
 }
```

