# Event-Driven DMN Quarkus Example

## Description

This example demonstrates the capability of the _Kogito Event-Driven Decisions AddOn_: to enable, when included as dependency of a simple DMN service,
to trigger evaluations of its models and receive the corresponding results via specific CloudEvents.

The source and destination of these events are two configured Kafka topics.

The main goal behind the addon is to allow Kogito DMN services to be used as part of an event processing pipeline.

## Installing and Running

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed
  - [Docker Engine](https://docs.docker.com/engine/) and [Docker Compose](https://docs.docker.com/compose/) installed

When using native image compilation, you will also need:
  - [GraalVM 19.3.1](https://github.com/oracle/graal/releases/tag/vm-19.3.1) installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Enable The AddOn

Like the other Kogito AddOns, the only required step to enable it is to include it as dependency in the [POM file](pom.xml):

```xml
<dependency>
  <groupId>org.kie</groupId>
  <artifactId>kie-addons-quarkus-events-decisions</artifactId>
</dependency>
```

The version is implicitly derived from the BOM imported in the `dependencyManagement` section.

### Configuration

The only configuration required is for the input and output topics. The Quarkus addon uses [MicroProfile Reactive Messaging](https://quarkus.io/guides/kafka)
under the hood and must be configured accordingly.

Here is the important section of [application.properties](src/main/resources/application.properties):

```properties
mp.messaging.incoming.kogito_incoming_stream.group.id=<group_id>
mp.messaging.incoming.kogito_incoming_stream.connector=smallrye-kafka
mp.messaging.incoming.kogito_incoming_stream.topic=<input_topic_name>
mp.messaging.incoming.kogito_incoming_stream.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

mp.messaging.outgoing.kogito_outgoing_stream.group.id=<group_id>
mp.messaging.outgoing.kogito_outgoing_stream.connector=smallrye-kafka
mp.messaging.outgoing.kogito_outgoing_stream.topic=<output_topic_name>
mp.messaging.outgoing.kogito_outgoing_stream.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

Insert the value you need in `<group_id>`, `<input_topic_name>` and `<output_topic_name>`. Pre-configured values already works if you follow this
guide without changes.

### Start test Kafka instance via Docker Compose

There's a useful [docker-compose.yml](docker-compose.yml) in the root that starts a dedicated Kafka instance for quick tests.

Simply start it with this command from the root of the repo:

```
docker-compose up -d
```

Once everything is started you can check the data contained in your small Kafka instance via [Kafdrop](https://github.com/obsidiandynamics/kafdrop) at `http://localhost:9000/`.

### Compile and Run in Local Dev Mode

```
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on Windows

```
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/dmn-quarkus-example-runner
```

Note: This does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon.

## Example Usage

Here is an example of a input event that triggers the evaluation of the [Traffic Violation](src/main/resources/Traffic%20Violation.dmn) model
included in this example. The `data` field contains the input context.

Just send this payload to the configured input topic:

```json
{
  "specversion": "1.0",
  "id": "a89b61a2-5644-487a-8a86-144855c5dce8",
  "source": "SomeEventSource",
  "type": "DecisionRequest",
  "subject": "TheSubject",
  "kogitodmnmodelname": "Traffic Violation",
  "kogitodmnmodelnamespace": "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF",
  "data": {
    "Driver": {
      "Age": 25,
      "Points": 13
    },
    "Violation": {
      "Type": "speed",
      "Actual Speed": 115,
      "Speed Limit": 100
    }
  }
}
```

And you should receive something similar to this in the output topic:

```json
{
  "specversion": "1.0",
  "id": "d54ace84-6788-46b6-a359-b308f8b21778",
  "source": "Traffic+Violation",
  "type": "DecisionResponse",
  "subject": "TheSubject",
  "kogitodmnmodelnamespace": "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF",
  "kogitodmnmodelname": "Traffic Violation",
  "data": {
    "Violation": {
      "Type": "speed",
      "Speed Limit": 100,
      "Actual Speed": 115
    },
    "calculateTotalPoints": "function calculateTotalPoints( driver, fine )",
    "Driver": {
      "Points": 13,
      "Age": 25
    },
    "Fine": {
      "Points": 3,
      "Amount": 500
    },
    "Should the driver be suspended?": "No"
  }
}
```

The `data` field contains the output context. Values of `id` fields will change, but the rest will be the same.

### Other examples

All the leaf subfolders of [the test events resource folder](src/test/resources/events) contain a pair of `input.json` and `output.json` files.

There's one for every possible variation in the structure of the input/output events supported by the addon. Feel free to try them all.
