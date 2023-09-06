# DMN Tracing Quarkus example

## Description

A simple DMN service to evaluate a loan approval and generate tracing events that might be consumed by the Trusty
service.

## Installing and Running

### Prerequisites

You will need:

- Java 11+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.8.6+ installed

When using native image compilation, you will also need:

- [GraalVM 19.3.1](https://github.com/oracle/graal/releases/tag/vm-19.3.1) installed
- Environment variable GRAALVM_HOME set accordingly
- Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be
  installed too. You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer
  to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites)
  for more details.

### Configuration of the tracing addon

The default configuration pushes the decision tracing events to the kafka topic `kogito-tracing-decision` and the DMN
models used by the kogito application to `kogito-tracing-model` under the group-id `kogito-runtimes`.
The configuration can be customized according to [https://quarkus.io/guides/kafka](https://quarkus.io/guides/kafka)
and [https://kafka.apache.org/documentation/#producerconfigs](https://kafka.apache.org/documentation/#producerconfigs)
using the prefix `mp.messaging.outgoing.kogito-tracing-decision.<property_name>`.
For example, in order to change the topic name for the decision tracing events, add the following line to
the `application.properties` file:

 ```
mp.messaging.outgoing.kogito-tracing-decision.topic=my-kogito-tracing-decision
```

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

## OpenAPI (Swagger) documentation

[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and
included in this service - to determine all available operations exposed by this service. For easy readability you can
visualize the OpenAPI definition file using a UI tool like for example
available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage
the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that
exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send
test requests.

## Example Usage

Once the service is up and running, you can use the following example to interact with the service.

### POST /LoanEligibility

Returns penalty information from the given inputs -- driver and violation:

Given inputs:

```json
{
  "Bribe": 0,
  "Client": {
    "age": 0,
    "existing payments": 0,
    "salary": 0
  },
  "Loan": {
    "duration": 0,
    "installment": 0
  },
  "SupremeDirector": "yes"
}
```

Curl command (using the JSON object above):

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"Bribe": 0,"Client": {"age": 0,"existing payments": 0,"salary": 0},"Loan": {"duration": 0,"installment": 0},"SupremeDirector": "yes"}' http://localhost:8080/LoanEligibility
```

or on Windows:

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"Bribe\": 0,\"Client\": {\"age\": 0,\"existing payments\": 0,\"salary\": 0},\"Loan\": {\"duration\": 0,\"installment\": 0},\"SupremeDirector\": \"yes\"}" http://localhost:8080/LoanEligibility
```

As response, penalty information is returned.

Example response:

```json
{
  "Eligibility": "No",
  "Judgement": null,
  "Loan": {
    "duration": 0,
    "installment": 0
  },
  "SupremeDirector": "yes",
  "Bribe": 0,
  "Client": {
    "existing payments": 0,
    "salary": 0,
    "age": 0
  },
  "Is Enough?": 0,
  "Decide": null
}
```

## Integration example with Trusty Service

When the tracing addon is enabled, the tracing events are emitted and pushed to a Kafka broker.
The [Trusty Service](https://github.com/kiegroup/kogito-apps/tree/main/trusty) can consume such events and store them on
a storage. The Trusty Service exposes then some api to consume the information that has been collected.
A `docker-compose` example is provided in the current folder. In particular, when `docker-compose up` is run, a Kafka
broker, an Infinispan container and the latest build of the trusty service configured to use Infinispan are deployed.
Once the services are up and running, after a decision has been evaluated, you can access the trusty service API to list
the evaluations at `localhost:8081/executions` for example.

## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with
the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
