# Process Service invocation

## Description

A quickstart project that processes travellers in the system. It's main purpose is to illustrate local service invocation.

This example shows

* invoking local service class that is a injectable bean
* control flow based on service calls

* New Travelers Diagram
<p align="center"><img width=75% height=50% src="docs/images/newTravelerProcess.png"></p>

* New Travelers Diagram Properties
<p align="center"><img src="docs/images/diagramProperties.png"></p>

* New Travelers Diagram	Properties
<p align="center"><img src="docs/images/diagramProperties2.png"></p>

* Store Traveler Service Call
<p align="center"><img src="docs/images/storeTravelerServiceCall.png"></p>

* Store Traveler Service Call
<p align="center"><img src="docs/images/storeTravelerServiceCall2.png"></p>

* Store Traveler Service Call
<p align="center"><img src="docs/images/storeTravelerServiceCall3.png"></p>

* Stored Traveler Gateway Yes Connector
<p align="center"><img src="docs/images/storedTravelerGatewayYesConnector.png"></p>

* Stored Traveler Gateway No Connector
<p align="center"><img src="docs/images/storedTravelerGatewayNoConnector.png"></p>

* Greet New  Traveler Service Call
<p align="center"><img src="docs/images/greetNewTravelerServiceCall.png"></p>

* Greet New  Traveler Service Call
<p align="center"><img src="docs/images/greetNewTravelerServiceCall2.png"></p>

* Audit Traveler Service Call
<p align="center"><img src="docs/images/auditTravelerServiceCall.png"></p>

* Audit Traveler Service Call
<p align="center"><img src="docs/images/auditTravelerServiceCall2.png"></p>

* Multi Params Process
<p align="center"><img src="docs/images/multiParamsProcess.png"></p>

* Multi Params Diagram Properties
<p align="center"><img src="docs/images/multiParamsDiagramProperties.png"></p>

* Multi Params Diagram Properties
<p align="center"><img src="docs/images/multiParamsDiagramProperties2.png"></p>

* Hello Service Calls
<p align="center"><img src="docs/images/helloServiceCalls.png"></p>

* Hello Service Calls
<p align="center"><img src="docs/images/helloServiceCalls2.png"></p>

## Build and run

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed

When using native image compilation, you will also need:
  - GraalVM 19.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

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
Note that the following configuration property needs to be added to `application.properties` in order to enable automatic registration of `META-INF/services` entries required by the workflow engine:
```
quarkus.native.auto-service-loader-registration=true
```

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/process-service-calls-quarkus-runner
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.


### Submit a traveller

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/travellers`  with following content

```json
{
"traveller" : {
  "firstName" : "John",
  "lastName" : "Doe",
  "email" : "jon.doe@example.com",
  "nationality" : "American",
  "address" : {
  	"street" : "main street",
  	"city" : "Boston",
  	"zipCode" : "10005",
  	"country" : "US" }
  }
}

```

Complete curl command can be found below:

```json
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"traveller" : { "firstName" : "John", "lastName" : "Doe", "email" : "jon.doe@example.com", "nationality" : "American","address" : { "street" : "main street", "city" : "Boston", "zipCode" : "10005", "country" : "US" }}}' http://localhost:8080/travellers
```

After the above command you should see a log similar to the following

<p align="center"><img src="docs/images/quarkusNewTravelerLog.png"></p>

### Calling a Simple Hello Service

To call Hello Service send a request to `http://localhost:8080/multiparams`  with following content

```json
{
  "name" : "John",
  "age" : 44,
}
```

Complete curl command can be found below:

```json
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name" : "John", "age" : 44}' http://localhost:8080/multiparams
```

After the above command you should see a log similar to the following

<p align="center"><img src="docs/images/quarkusHelloServiceLog.png"></p>
