# Kogito Serverless Workflow - Callback Example

## Description

This example contains a simple workflow service that illustrate callback state usage. 
A callback is a state that invokes an action and wait for an event, so this example needs an event broker.
This example consist of just one callback state. Its action calls a service that publish the event type that is expected by its eventRef. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

## Infrastructure requirements

This quickstart requires an Apache Kafka to be available and by default expects it to be on default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

To publish and consume the event, topic "move" is used. 


## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

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
./target/serverless-workflow-callback-quarkus-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/callback'

```

Complete curl command can be found below:

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/callback
```


After a while (note that to you need give time for event to be consumed)  you should see the log message printed in quarkus:

```text
This has been injected by the event
```

