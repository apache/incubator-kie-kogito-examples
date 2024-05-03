# Kogito Serverless Workflow - Error Example

## Description

This example contains a simple workflow service that illustrate error handling. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

The workflow check if the number is odd or even and print a message indicating that. 
The main feature of this demo is that if the number is odd, an exception is thrown, and it is the exception error handling the one that sets the odd message. 

Hence, this workflow expects JSON input containing a natural number. This number is passed using a service operation to `EvenService` java class. If the number is even, the workflow moves to the next defined state, injecting "even" `numberType`. But if the number is odd, the class throws an `IllegalArgumentException`. This exception is handled and redirected to odd inject node by using [inline workflow error handling](https://github.com/serverlessworkflow/specification/blob/main/specification.md#Workflow-Error-Handling).  This basically consists on adding `onErrors` field, where the expected exception is specified in `code` and the target state (a node injecting "odd" `numberType`) in `transition`. Finally, both execution paths finish on the same node, which prints the calculated `eventType`.

As per 0.8 version of the specification, there is no standard way to set a process in error. To do that, users can use a custom metadata key called `errorMessage` which will contain either the error message to be associated to the process instance or an expression that returns the error message to associated to the process instance. In addition to the workflow described before, this example includes a file called `errorWithMEtadata.sw.json` that illustrate the usage of such metadata. 


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
./target/serverless-workflow-error-quarkus-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/error
with following content 

```json
{
  "number": 2
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"number": 2}' http://localhost:8080/error
```


In Quarkus you should see the log message printed:

```text
even
```

If you would like to check odd number

```json
{
  "number": 1
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"number": 1}' http://localhost:8080/error
```

In Quarkus you should see the log message printed:

```text
odd
```
