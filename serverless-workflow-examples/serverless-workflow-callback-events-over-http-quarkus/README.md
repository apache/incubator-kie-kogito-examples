# Kogito Serverless Workflow - Callback Events Over HTTP Quarkus Example

## Description

This example contains a simple workflow service that illustrates callback state using OpenAPI callbacks functionality.
A callback is a state that invokes an action and wait for an event (event that will be eventually fired by the external service notified by the action).
This example consists of a callback state that waits for an event to arrive at the wait channel. Its action calls an external service that publishes the wait event over HTTP.
After consuming the wait event, the workflow prints the message received in the wait event and ends the workflow.

To go further on using HTTP with Reactive Messaging, take a look at [this article](https://quarkiverse.github.io/quarkiverse-docs/quarkus-reactive-messaging-http/dev/reactive-messaging-http.html).

For information related to Open API callbacks functionality, please refer [this document](https://swagger.io/docs/specification/callbacks/).

The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).


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
  
Run the following commands from the callback-workflow directory as per the selected mode of running.
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
./target/serverless-workflow-compensation-quarkus-{version}-runner
```
 In another terminal, run the callback event service using the below command

```sh
mvn clean package quarkus:dev -Dquarkus.http.port=8181
```

### Submit a request

The service based on the JSON workflow definition can be accessed by sending a request to http://localhost:8080/callback
with the following content

```json
{
  "message": "Hello"
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"message": "Hello"}' http://localhost:8080/callback
```

Should return something like this ("id" will change):

```json
{
  "id":"f9a75f77-7269-4b18-93cd-2955e3406cd4",
  "workflowdata":{
    "message":"New Event"
  }
}
```