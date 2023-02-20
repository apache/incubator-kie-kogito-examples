# Kogito Serverless Workflow - Consuming Events Over HTTP Example

## Description

This example contains a simple workflow service that consumes events over HTTP. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).
It's a simple workflow that, after starting, waits for an event to be published over HTTP.
Then the workflow prints the event content to the console when the event is received.

To go further on using HTTP with Reactive Messaging, take a look at [this article](https://quarkus.io/guides/reactive-messaging-http.html).

This is the infrastructure required to integrate with [Knative Eventing](https://knative.dev/docs/eventing/).

> Knative Eventing uses standard HTTP POST requests to send and receive events between event producers and sinks. These events conform to the CloudEvents specifications, which enables creating, parsing, sending, and receiving events in any programming language.

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

### Submit a request

The service based on the JSON workflow definition can be started by sending a request to http://localhost:8080/start'
with following content 

```json
{
  "message": "Hello"
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"message": "Hello"}' http://localhost:8080/start
```

Should return something like this ("id" will change):

```json
{
  "id":"f9a75f77-7269-4b18-93cd-2955e3406cd4",
  "workflowdata":{
    "message":"Hello"
  },
  "waitForEvent_9":null
}
```

You should see the message printed to the console in Quarkus log.
```shell
[ "Hello" ]
```

At this point, your workflow is waiting for an event to be published over HTTP. Send the following event: (You should
use in "kogitoprocrefid" field the id returned by the previous request)

```json
{
  "specversion":"1.0",
  "source":"",
  "type":"move",
  "time":"2022-01-06T15:35:29.967831-03:00",
  "kogitoprocrefid":"f9a75f77-7269-4b18-93cd-2955e3406cd4",
  "data":{
    "move":"This has been injected by the event"
  }
}
```

Complete curl command can be found below:

```shell
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"specversion":"1.0","id":"e4604756-f58e-440e-9619-484b92408308","source":"","type":"move","time":"2022-01-06T15:35:29.967831-03:00","kogitoprocrefid":"f9a75f77-7269-4b18-93cd-2955e3406cd4","data":{"move":"This has been injected by the event"}}' http://localhost:8080/
```

The workflow will consume the event and print the message you sent to the console.

```shell
[ "Hello", "This has been injected by the event" ]
```