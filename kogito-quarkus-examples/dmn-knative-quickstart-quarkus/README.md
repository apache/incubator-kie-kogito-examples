# Decisions with Knative Eventing

## Description

A quickstart project that deals with "Traffic Violation" DMN model. It illustrates how easy it is to make the Kogito decisions to work with Knative
Eventing.

This example shows

* consuming events from a Knative Eventing broker and for each event evaluate the DMN model
* the model is then evaluated and the output result is sent to Knative broker

## Infrastructure requirements

This quickstart requires Knative Eventing to be available in your cluster:

* Install [minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/)
* Install [Knative Eventing](https://knative.dev/docs/install/) in your minikube cluster

For local testing only you can use [Podman](https://podman.io/getting-started/installation.html) or Docker to simulate an application receiving your
events.

## Build and run

### Prerequisites

You will need:

- Java 17+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.9.6+ installed

When using native image compilation, you will also need:

- GraalVM 19.3+ installed
- Environment variable GRAALVM_HOME set accordingly
- GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
- Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to
  GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```sh
K_SINK=http://localhost:8181 mvn clean compile quarkus:dev
```

[`K_SINK` is the environment variable injected by the Knative Eventing platform](https://knative.dev/docs/eventing/samples/sinkbinding/#create-our-sinkbinding)
once we deploy the application on a Kubernetes cluster. Instead of _hardcoding_ the endpoint where we are going to send our produced messages, we rely
on Knative to inject this information in runtime.

The environment variable will be assigned to the Quarkus HTTP connector in runtime: `mp.messaging.outgoing.kogito_outgoing_stream.url=${K_SINK}`.

For local tests we will mock the endpoint where the produced messages supposed to be delivered.

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need
to redeploy or restart your running application.

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
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
./target/dmn-knative-quickstart-quarkus-runner
```

### Use the application locally

First thing, fire up the sink application using podman/docker:

```shell script
$ podman run --rm -it -p 8181:8080 gcr.io/knative-releases/knative.dev/eventing-contrib/cmd/event_display
```

This is the same image used by Knative Eventing demos. It's running on port 8181 to not clash with the example application.

Then run the application with:

```shell script
$ K_SINK=http://localhost:8181 mvn clean quarkus:dev

2021-07-05 17:09:09,415 INFO  [org.kie.kog.cod.api.uti.AddonsConfigDiscovery] (build-12) Performed addonsConfig discovery, found: AddonsConfig{usePersistence=false, useTracing=false, useMonitoring=false, usePrometheusMonitoring=false, useCloudEvents=true, useExplainability=false, useProcessSVG=false, useEventDrivenDecisions=true}
2021-07-05 17:09:09,418 INFO  [org.kie.kog.cod.cor.uti.ApplicationGeneratorDiscovery] (build-12) Generator discovery performed, found [openapispecs, decisions]
2021-07-05 17:09:09,485 INFO  [org.kie.kog.cod.dec.DecisionValidation] (build-12) Initializing DMN DT Validator...
2021-07-05 17:09:09,485 INFO  [org.kie.kog.cod.dec.DecisionValidation] (build-12) DMN DT Validator initialized.
2021-07-05 17:09:09,485 INFO  [org.kie.kog.cod.dec.DecisionValidation] (build-12) Analysing decision tables in DMN Model 'Traffic Violation' ...
2021-07-05 17:09:09,488 INFO  [org.kie.kog.cod.dec.DecisionValidation] (build-12)  analysis for decision table 'Fine':
2021-07-05 17:09:09,489 WARN  [org.kie.kog.cod.dec.DecisionValidation] (build-12)   Columns: [Violation.Type] relate to FEEL string values which can be enumerated for the inputs; Gap analysis skipped.
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-07-05 17:09:09,890 WARN  [io.sma.rea.mes.provider] (Quarkus Main Thread) SRMSG00207: Some components are not connected to either downstream consumers or upstream producers:
        - SubscriberMethod{method:'org.kie.kogito.addon.cloudevents.quarkus.QuarkusCloudEventPublisher#onEvent', incoming:'kogito_incoming_stream'} has no upstream

2021-07-05 17:09:09,892 INFO  [org.kie.kog.add.clo.qua.QuarkusKogitoExtensionInitializer] (Quarkus Main Thread) Registered Kogito CloudEvent extension
2021-07-05 17:09:09,902 INFO  [io.quarkus] (Quarkus Main Thread) dmn-knative-quickstart-quarkus 999-SNAPSHOT on JVM (powered by Quarkus 2.0.0.Final) started in 0.610s. Listening on: http://localhost:8080
2021-07-05 17:09:09,903 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2021-07-05 17:09:09,903 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, kogito-decisions, resteasy, resteasy-jackson, servlet, smallrye-context-propagation, smallrye-reactive-messaging, vertx, vertx-web]
``` 

Now send a message to the application on 8080 port using the [cloud event format](https://github.com/cloudevents/spec) with `curl`:

```shell script
$ curl -vvv -X POST \
    -H "content-type: application/json" \
    -H "ce-specversion: 1.0" \
    -H "ce-id: a89b61a2-5644-487a-8a86-144855c5dce8" \
    -H "ce-source: /from/localhost" \
    -H "ce-type: DecisionRequest" \
    -H "ce-subject: 123abcdefg" \
    -H "ce-kogitodmnmodelname: Traffic Violation" \
    -H "ce-kogitodmnmodelnamespace: https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF" \
    -d '{"Driver": { "Age": 35, "Points": 3 }, "Violation": { "Type": "speed", "Actual Speed": 115, "Speed Limit": 100 }}' \
    http://localhost:8080
```

You should see an output like this one in the running container terminal:

```shell script
☁️  cloudevents.Event
Validation: valid
Context Attributes,
  specversion: 1.0
  type: DecisionResponse
  source: Traffic+Violation
  subject: 123abcdefg
  id: 033ac571-fde1-42e1-87c8-b675236b1030
Extensions,
  kogitodmnmodelname: Traffic Violation
  kogitodmnmodelnamespace: https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF
Data,
  {"Violation":{"Type":"speed","Speed Limit":100,"Actual Speed":115,"Code":null,"Date":null},"calculateTotalPoints":"function calculateTotalPoints( driver, fine )","Driver":{"Points":3,"State":null,"City":null,"Age":35,"Name":null},"Fine":{"Points":3,"Amount":500},"Should the driver be suspended?":"No"}
```
