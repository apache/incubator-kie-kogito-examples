# Kogito Serverless Workflow - Correlation with Callback Example

## Description

This example contains a workflow service to demonstrate correlation feature using callback states and events. 
Each callback state withing the workflow publishes an event and wait for a response event, 
there is an incoming event, it is matched with the proper workflow instance by using the correlation attribute, in this case it is the `userid`. So for every incoming event the userid is used to properly find and trigger the proper workflow instance. The correlation is defined in the [workflow definition file](src/main/resources/correlation.sw.json) that is described using JSON format as defined in the [CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

```json
"correlation": [
    {
    "contextAttributeName": "userid"
    }
]
```
Events should be in CloudEvent format and the correlation attribute should be defined as an extension attribute, in this case `userid`.

The workflow example is started by events as well, so a start event should be published with the same correlation attribute `userid, that will be used to match correlations for the started workflow instance. 
 
In the example the event broker used to publish/receive the events is Kafka, and the used topics are the same described as the event types in the workflow definition.


```json
{
  "name": "newAccountEvent",
  "source": "",
  "type": "newAccountEventType",
  "correlation": [
    {
      "contextAttributeName": "userid"
    }
  ]
}
```
For simplicity, the events are published and consumed in the same application running the workflow, but in a real use case they should come from different services interacting with the workflow, see [EventsService](src/main/java/org/kie/kogito/examples/EventsService.java).

To start the workflow as mentioned, it is required an event to be published which is going to be consumed by the workflow service starting a new instance. A helper REST endpoint was recreated to simplify this step, so once a POST request is received it publishes the start event to the broker see [WorkflowResource](src/main/java/org/kie/kogito/examples/WorkflowResource.java).

All eventing configuration and the broker parameters are in done in the [application.properties](src/main/resources/application.properties). 

## Infrastructure requirements

### Kafka

This quickstart requires an Apache Kafka to be available and by default expects it to be on default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

To publish and consume the event, topic "move" is used. 

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:

```sh
docker-compose up
```

In this way a container for Kafka will be started on port 9092.

### PostgreSQL

Alternatively, you can run this example using persistence with a PostgreSQL server.

Configuration for setting up the connection can be found in [applications.properties](src/main/resources/application.properties) file, which
follows the Quarkus JDBC settings, for more information please check [JDBC Configuration Reference](https://quarkus.io/guides/datasource#jdbc-configuration).

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:

```sh
docker-compose up
```

In this way a container for PostgreSQL will be started on port 5432.

Tip: If you get permission denied while creating the postgres container, consider to use SELinux context.
Update the following line:
```yaml
    - ./sql:/docker-entrypoint-initdb.d
```
to
```yaml
    - ./sql:/docker-entrypoint-initdb.d:Z
```

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

### Compile and Run in JVM mode using PostgreSQL persistence

To enable persistence, please append `-Ppersistence` to your Maven command.
That will ensure the correct dependencies are in place, and automatically set the required properties to connect
with the PostgreSQL instance from the provided docker compose.

```sh
mvn clean package -Peristence 
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```sh
./target/serverless-workflow-correlation-quarkus-{version}-runner
```

### Start a workflow

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/account/{userid}

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/account/12345
```

After a while (note that to you need give time for event to be consumed)  you should see the log message printed in the console, and the workflow is completed.

```text
2022-05-12 11:02:15,891 INFO  [org.kie.kog.ser.eve.imp.ProcessEventDispatcher] (kogito-event-executor-0) Starting new process instance with signal 'newAccountEventType'
2022-05-12 11:02:18,909 INFO  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-9) SRMSG18256: Initialize record store for topic-partition 'validateAccountEmail-0' at position 16.
2022-05-12 11:02:18,919 INFO  [org.kie.kog.exa.EventsService] (pool-1-thread-1) Validate Account received. Workflow data JsonCloudEventData{node={"email":"test@test.com","userId":"12345"}}
2022-05-12 11:02:19,931 INFO  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-5) SRMSG18256: Initialize record store for topic-partition 'validatedAccountEmail-0' at position 16.
2022-05-12 11:02:20,962 INFO  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-8) SRMSG18256: Initialize record store for topic-partition 'activateAccount-0' at position 16.
2022-05-12 11:02:20,971 INFO  [org.kie.kog.exa.EventsService] (pool-1-thread-1) Activate Account received. Workflow data JsonCloudEventData{node={"email":"test@test.com","userId":"12345"}}
2022-05-12 11:02:21,994 INFO  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-6) SRMSG18256: Initialize record store for topic-partition 'activatedAccount-0' at position 7.
2022-05-12 11:02:22,006 INFO  [org.kie.kog.exa.EventsService] (kogito-event-executor-0) Complete Account Creation received. Workflow data {"email":"test@test.com","userId":"12345"}, KogitoProcessInstanceId 0cef0eef-06c8-4433-baea-505fa8d45f68 
```