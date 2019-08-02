# jBPM + Quarkus example

## Description

A simple process service for ordering items, as a sequence of a script task (writing out some debug info) and a call activity invoking a sub-process, using a custom `Order` data element.

The sub-process invokes a custom Java service `CalculationService.calculateTotal`, followed by a user task to verify the order.

Based on these two processes (defined using BPMN 2.0 format), the custom data object and custom Java service, a new service is generated that exposes REST operations to create new orders (following the steps as defined in the main and sub-process), or to list and delete active orders.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

When using native image compilation, you will also need: 
  - GraalVM 1.0.0-rc16 installed - note that GraalVM 19.0+ does not (yet) work with Quarkus for native image compilation, this should be updated soon but please use 1.0.0.rc16 until then
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```
mvn clean package quarkus:dev    
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```
./target/jbpm-quarkus-example-{version}-runner
```

### Running with persistence enabled

Kogito supports runtime persistence that is backed by Inifinispan. So to be able to enable this you need to have 
Infinispan server installed and available over the network. By default it expects it to be at (it can be configured via application.properties file located in src/main/resources)

```
localhost:11222
```

You can install Inifinispan server by downloading it from [https://infinispan.org/download/](official website) version to be used in 10.0.0.Beta4

Once Inifispan is up and running you can build this project with `-Ppersistence` to enable additional processing
during the build. Next you start it in exact same way as without persistence.

This extra profile in maven configuration adds additional dependencies needed to work with infinispan as persistent store. 


## Swagger documentation

You can take a look at the [swagger definition](http://localhost:8080/docs/swagger.json) - automatically generated and included in this service - to determine all available operations exposed by this service.  For easy readability you can visualize the swagger definition file using a swagger UI like for example available [here](https://editor.swagger.io). In addition, various clients to interact with this service can be easily generated using this swagger definition.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /orders

Allows to create a new order with the given data:

```sh
curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" \
    -X POST http://localhost:8080/orders
```

As response the updated order is returned.

### GET /orders

Returns list of orders currently active:

```sh
curl -X GET http://localhost:8080/orders
```

As response an array of orders is returned.

### GET /orders/{id}

Returns order with given id (if active):

```sh
curl -X GET http://localhost:8080/orders/1
```

As response a single order is returned if found, otherwise no content (204) is returned.

### DELETE /orders/{id}

Cancels order with given id

```sh
curl -X DELETE http://localhost:8080/orders/1
```