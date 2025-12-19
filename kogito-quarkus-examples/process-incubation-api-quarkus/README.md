# Process Incubation API

## Description

This quickstart project demonstrate how to use the Kogito Public API (*Incubation*). It disables the predefined generated REST endpoint and instead uses the Public API to define a custom HTTP resource.

The custom REST endpoint evaluates a process that expects a name:

- when the name is supplied via REST request, it prints a "hello" message to screen and return that same message as a REST response.
- the quickstart uses the public API to define a custom REST endpoint instead of codegen.

*Incubation* means that this API is experimental, but it is part of a regular release for early access and to gather community feedback.

## Build and run

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed

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

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

### Submit a request

To make use of this application it is as simple as sending a request to `http://localhost:8080/custom-rest-process`  with the following content

```json
{
    "name" : "john"
}

```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name" : "john"}' http://localhost:8080/custom-rest-process
```

Response should be similar to:

```json
{
    "name":"john",
    "message":"Hello john"
}
```

And also in Quarkus log you should see a log entry:

```
Hello john"
```

