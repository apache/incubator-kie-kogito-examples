# Event-Driven DRL Quarkus Example

## Description

This example demonstrates the capability of the _Kogito Event-Driven Rules AddOn_: to enable, when included as dependency of a simple service containing
DRL files, to trigger evaluations of its queries and receive the corresponding results via specific CloudEvents.

The source and destination of these events are two configured Kafka topics.

The main goal behind the addon is to allow Kogito DRL services to be used as part of an event processing pipeline.

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
  <groupId>org.drools</groupId>
  <artifactId>drools-addons-quarkus-events-rules</artifactId>
</dependency>
```

The version is implicitly derived from the `kogito-quarkus-bom` included in the `dependencyManagement` section.

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

Here is an example of a input event that triggers the evaluation of the [Loan Unit](src/main/resources/org/kie/kogito/queries/RuleUnitQuery.drl) queries
included in this example. The `data` field contains the query input.

Just send this payload to the configured input topic:

```json
{
  "specversion": "1.0",
  "id": "a89b61a2-5644-487a-8a86-144855c5dce8",
  "source": "SomeEventSource",
  "type": "RulesRequest",
  "subject": "TheSubject",
  "kogitoruleunitid": "org.kie.kogito.queries.LoanUnit",
  "kogitoruleunitquery": "FindApproved",
  "data": {
    "maxAmount": 5000,
    "loanApplications": [
      {
        "id": "ABC10001",
        "amount": 2000,
        "deposit": 100,
        "applicant": {
          "age": 45,
          "name": "John"
        }
      },
      {
        "id": "ABC10002",
        "amount": 5000,
        "deposit": 100,
        "applicant": {
          "age": 25,
          "name": "Paul"
        }
      },
      {
        "id": "ABC10015",
        "amount": 1000,
        "deposit": 100,
        "applicant": {
          "age": 12,
          "name": "George"
        }
      }
    ]
  }
}
```

And you should receive something similar to this in the output topic:

```json
{
  "specversion": "1.0",
  "id": "d54ace84-6788-46b6-a359-b308f8b21778",
  "source": "find-approved",
  "type": "RulesResponse",
  "subject": "TheSubject",
  "kogitoruleunitid": "org.kie.kogito.queries.LoanUnit",
  "kogitoruleunitquery": "FindApproved",
  "data": [
    {
      "id": "ABC10001",
      "applicant": {
        "name": "John",
        "age": 45
      },
      "amount": 2000,
      "deposit": 100,
      "approved": true
    }
  ]
}
```

The `data` field contains the query output. Values of `id` fields will change, but the rest will be the same.

### Other examples

All the leaf subfolders of [the test events resource folder](src/test/resources/events) contain a pair of `input.json` and `output.json` files.

There's one for every possible variation in the structure of the input/output events supported by the addon. Feel free to try them all.
