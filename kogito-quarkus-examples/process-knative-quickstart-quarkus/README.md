# Process with Knative Eventing

## Description

A quickstart project that deals with traveller processing carried by rules. It illustrates how easy it is to make the Kogito processes and rules to work with Knative Eventing. This project is based on
the example [Process with Kafka](https://github.com/apache/incubator-kie-kogito-examples/tree/main/kogito-quarkus-examples/process-kafka-quickstart-quarkus).

This example shows

* consuming events from a Knative Eventing broker and for each event start new process instance
* each process instance is expecting a traveller information in JSON format
* traveller is then processed by rules and based on the outcome of the processing (processed or not) traveller is
    * if successfully processed traveller information is logged and then updated information is send to Knative broker
    * if not processed traveller info is logged and then process instance finishes emitting the event `skiptraveller` to Knative broker

![](docs/images/process.png)

* Diagram Properties (top)
  ![](docs/images/diagramProperties.png)

* Diagram Properties (bottom)
  ![](docs/images/diagramProperties2.png)

* Diagram Properties (process variables)
  ![](docs/images/diagramProperties3.png)

* Start Message
  ![](docs/images/startMessage.png)

* Start Message (Assignments)
  ![](docs/images/startMessageAssignments.png)

* Process Traveler Business Rule (top)
  ![](docs/images/processTravelerBusinessRule.png)

* Process Traveler Business Rule (bottom)
  ![](docs/images/processTravelerBusinessRule2.png)

* Process Traveler Business Rule (Assignments)
  ![](docs/images/processTravelerBusinessRuleAssignments.png)

* Process Traveler Gateway
  ![](docs/images/processedTravelerGateway.png)

* Process Traveler Gateway Yes Connector
  ![](docs/images/processedTravelerYesConnector.png)

* Process Traveler Gateway No Connector
  ![](docs/images/processedTravelerNoConnector.png)

* Log Traveler Script Task
  ![](docs/images/logTravelerScriptTask.png)

* Skip Traveler Script Task
  ![](docs/images/skipTravelerScriptTask.png)

* Processed Traveler End Message
  ![](docs/images/processedTravelerEndMessage.png)

* Processed Traveler End Message (Assignments)
  ![](docs/images/processedTravelerEndMessageAssignments.png)

* Skip Traveler End
  ![](docs/images/skipTraveler.png)

## Infrastructure requirements

This quickstart requires Knative Eventing to be available in your cluster:

* Install [minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/)
* Install [Knative Eventing](https://knative.dev/docs/install/) in your minikube cluster

For local testing only you can use [Podman](https://podman.io/getting-started/installation.html) or Docker to simulate an application receiving your events.

## Build and run

### Prerequisites

You will need:

- Java 11+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.8.6+ installed

When using native image compilation, you will also need:

- GraalVM 19.3+ installed
- Environment variable GRAALVM_HOME set accordingly
- GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
- Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more
  details.

### Compile and Run in Local Dev Mode

```sh
K_SINK=http://localhost:8181 mvn clean compile quarkus:dev
```

[`K_SINK` is the environment variable injected by the Knative Eventing platform](https://knative.dev/docs/eventing/samples/sinkbinding/#create-our-sinkbinding)
once we deploy the application on a Kubernetes cluster. Instead of _hardcoding_ the endpoint where we are going to send our produced messages, we rely on Knative to inject this information in runtime.

The environment variable will be assigned to the SmallRye HTTP connector in runtime: `mp.messaging.outgoing.processedtravellers.url=${K_SINK}`.

For local tests we will mock the endpoint where the produced messages supposed to be delivered.

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.

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
./target/process-kafka-quickstart-quarkus-runner
```

### OpenAPI (Swagger) documentation

[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by
this service.

For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the
[Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/)
that you can use to look at available REST endpoints and send test requests.

### Use the application locally

First thing, fire up the sink application using podman/docker:

```shell script
$ podman run --rm -it -p 8181:8080 gcr.io/knative-releases/knative.dev/eventing-contrib/cmd/event_display
```

This is the same image used by Knative Eventing demos. It's running on port 8181 to not clash with the example application.

Then run the application with:

```shell script
$ K_SINK=http://localhost:8181 mvn clean quarkus:dev

2021-05-18 14:50:47,574 INFO  [org.kie.kog.cod.api.uti.AddonsConfigDiscovery] (build-24) Performed addonsConfig discovery, found: AddonsConfig{usePersistence=false, useTracing=false, useMonitoring=false, usePrometheusMonitoring=false, useCloudEvents=true, useExplainability=false, useProcessSVG=false}
2021-05-18 14:50:47,777 INFO  [org.kie.kog.cod.cor.uti.ApplicationGeneratorDiscovery] (build-24) Generator discovery performed, found [openapispecs, processes, rules, decisions, predictions]
2021-05-18 14:50:48,726 INFO  [org.kie.kog.cod.api.uti.AddonsConfigDiscovery] (build-31) Performed addonsConfig discovery, found: AddonsConfig{usePersistence=false, useTracing=false, useMonitoring=false, usePrometheusMonitoring=false, useCloudEvents=true, useExplainability=false, useProcessSVG=false}
2021-05-18 14:50:48,730 INFO  [org.kie.kog.qua.com.dep.KogitoQuarkusResourceUtils] (build-31) No Java source to compile
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-05-18 14:50:50,046 WARN  [io.sma.rea.mes.provider] (Quarkus Main Thread) SRMSG00207: Some components are not connected to either downstream consumers or upstream producers:
	- SubscriberMethod{method:'org.kie.kogito.addon.cloudevents.quarkus.QuarkusCloudEventPublisher#onEvent', incoming:'kogito_incoming_stream'} has no upstream

2021-05-18 14:50:50,111 INFO  [org.kie.kog.add.clo.qua.QuarkusKogitoExtensionInitializer] (Quarkus Main Thread) Registered Kogito CloudEvent extension
2021-05-18 14:50:50,114 INFO  [org.kie.kog.ser.eve.imp.AbstractMessageConsumer] (Quarkus Main Thread) Consumer for class org.acme.travel.Traveller started.
2021-05-18 14:50:50,164 INFO  [io.quarkus] (Quarkus Main Thread) process-knative-quickstart-quarkus 999-SNAPSHOT on JVM (powered by Quarkus 1.13.3.Final) started in 3.118s. Listening on: http://localhost:8080
2021-05-18 14:50:50,164 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2021-05-18 14:50:50,164 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, kogito-decisions, kogito-predictions, kogito-processes, kogito-rules, mutiny, rest-client, rest-client-jackson, resteasy, resteasy-jackson, servlet, smallrye-context-propagation, smallrye-health, smallrye-openapi, smallrye-reactive-messaging, swagger-ui, vertx, vertx-web]
``` 

Now send a message to the application on 8080 port using the [cloud event format](https://github.com/cloudevents/spec) with `curl`:

```shell script
$ curl -X POST \
      -H "content-type: application/json"  \
      -H "ce-specversion: 1.0"  \
      -H "ce-source: /from/localhost"  \
      -H "ce-type: travellers"  \
      -H "ce-id: 12345"  \
      -d '{"firstName": "Jan", "lastName": "Kowalski", "email": "jan.kowalski@example.com", "nationality": "Polish"}' \
  http://localhost:8080
```

You should see an output like this one in the running container terminal:

```shell script
☁️  cloudevents.Event
Validation: valid
Context Attributes,
  specversion: 1.0
  type: processedtravellers
  source: /process/Travelers
  id: f37c3856-3ee4-47ed-8daf-d81ff6c8d695
  time: 2021-05-18T17:51:50.056553Z
Extensions,
  kogitoprocid: Travelers
  kogitoprocinstanceid: c70a8abc-eaa6-4be9-85e9-2ebf9bcb101b
  kogitousertaskist: 1
Data,
  {"firstName":"Jan","lastName":"Kowalski","email":"jan.kowalski@example.com","nationality":"Polish","processed":true}
```

There are a bunch of extension attributes that starts with `kogito` to provide some context of the execution and the event producer.

To take the other path of the process send this message to your application:

```shell script
$ curl -X POST \
      -H "content-type: application/json"  \
      -H "ce-specversion: 1.0"  \
      -H "ce-source: /from/localhost"  \
      -H "ce-type: travellers"  \
      -H "ce-id: 12346"  \
      -d '{"firstName": "Jane", "lastName": "Doe", "email": "jane.doe@example.com", "nationality": "American"}' \
  http://localhost:8080
```

this will not result in a message sent to the broker:

```shell script
This system can't deal with American
Skipping traveller Traveller [firstName=Jane, lastName=Doe, email=jane.doe@example.com, nationality=American, processed=false]
```

## Deploying in Minikube

We have prepared a `knative` Maven profile to build the service image locally and all the Knative resources you need
to get started.

Just make sure your cluster has [Knative Eventing available](https://knative.dev/docs/eventing/getting-started/):

1. [Install Knative](https://knative.dev/docs/getting-started/)
2. Install the `KogitoSource` [via command line](https://github.com/knative-sandbox/eventing-kogito#installation).
3. Run `eval $(minikube docker-env)` to build the image directly into the Minikube registry.
4. Run `mvn clean install -Pknative -Dnamespace=<your namespace>` to build the image and the Knative resources for your application to run.
5. Apply the objects created for you with `kubectl apply -f target/kubernetes/*.yml`. It will deploy the objects from `knative.yml` and `kogito.yml` generated files.
6. Run `curl` from the terminal like you did in the previously steps. To see what's going on, just query for the Knative service `event-display`. You should see something like:

```
☁️  cloudevents.Event
Validation: valid
Context Attributes,
  specversion: 1.0
  type: process.travelers.processedtravellers
  source: /process/Travelers/2f692fd9-fff8-4b0a-bb64-96d1a4772490
  id: 29e43b17-3a70-4b46-aca0-7ab8e2133eee
  time: 2020-08-10T20:52:39.383346Z
Extensions,
  knativearrivaltime: 2020-08-10T20:52:39.391404032Z
  knativehistory: default-kne-trigger-kn-channel.kogito.svc.cluster.local
  kogitoprocessid: Travelers
  kogitoprocessinstanceid: 2f692fd9-fff8-4b0a-bb64-96d1a4772490
  kogitoprocessinstancestate: 1
Data,
  {"firstName":"Jan","lastName":"Kowalski","email":"jan.kowalski@example.com","nationality":"Polish","processed":true}
```

The diagram below illustrates the Knative objects architecture for this demo:

![](docs/images/knative-diagram.png)

### Accessing the Service on Minikube

Ideally, you installed Knative on Minikube via [their quickstart](https://knative.dev/docs/getting-started/). Doing so, you will have installed nip.io DNS and will be able to access the services via
their exposed Knative Routes.

Alternatively, if you installed via Knative Operators and Istio, you will need to follow this procedure in order to access the service:

1. Run `minikube tunnel`
2. Define the Ingress Gateway `INGRESSGATEWAY=istio-ingressgateway`
3. Get the Gateway IP with:
   ```shell
   export GATEWAY_IP=`kubectl get svc $INGRESSGATEWAY --namespace istio-system \
    --output jsonpath="{.status.loadBalancer.ingress[*]['ip']}"`
   ```
4. Run the `curl` command using the Gateway URL. For example:

   ```shell
    $ curl -X POST \
    -H "content-type: application/json"  \
    -H "ce-specversion: 1.0"  \
    -H "ce-source: /from/localhost"  \
    -H "ce-type: travellers"  \
    -H "ce-id: 12346"  \
    -d '{"firstName": "Jane", "lastName": "Doe", "email": "jane.doe@example.com", "nationality": "American"}' \
    http://${GATEWAY_IP} --header "Host:process-knative-quickstart-quarkus.kogito.example.com"
   ```
