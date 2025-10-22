# Process + SpringBoot example

## Description

A simple process service for ordering items, as a sequence of a script task (writing out some debug info) and a call activity invoking a sub-process, using a custom `Order` data element.

The sub-process invokes a custom Java service `CalculationService.calculateTotal`, followed by a user task to verify the order.

Based on these two processes (defined using BPMN 2.0 format), the custom data object and custom Java service, a new service is generated that exposes REST operations to create new orders (following the steps as defined in the main and sub-process), or to list and delete active orders.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.9+ installed

### Compile and Run in Local Dev Mode

```
mvn clean compile spring-boot:run
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/jbpm-springboot-example.jar
```

or on windows

```
mvn clean package
java -jar target\jbpm-springboot-example.jar
```

### Running with persistence enabled

Kogito runtime supports multiple persistence types, including Infinispan.
In order to use the Infinispan based persistence, you need to have a Infinispan server installed and available over the network.
The default configuration, expects the server to be running on:
```
infinispan.remote.server-list=localhost:11222
```
If you need to change it, you can do so by updating the application.properties file located in src/main/resources.

You can install Infinispan server by downloading version 12.x from the [official website](https://infinispan.org/download/).

Once Infinispan is up and running you can build this project with `-Ppersistence` to enable additional processing during the build. Next you start it in exact same way as without persistence.

This extra profile in maven configuration adds additional dependencies needed to work with Infinispan as persistent store.

### Running with events enabled

Kogito supports cloud events using Kafka as message broker. So to be able to enable this you need to have
Kafka cluster installed and available over the network. Refer to [Kafka Apache site](https://kafka.apache.org/quickstart) to more information about how to install. By default it expects it to be at (it can be configured via application.properties file located in src/main/resources):

```
spring.kafka.bootstrap-servers=localhost:9092
```

Kogito will use the following Kafka topics to listen for cloud events:

* `kogito-processinstances-events` - used to emit events by kogito that can be consumed by data index service and other services
* `kogito-usertaskinstances-events` -used to emit events by kogito that can be consumed by data index service

Once Kafka is up and running you can build this project with `-Pevents` to enable additional processing during the build. This extra profile in maven configuration adds additional dependencies needed to work with Cloud Events.

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /orders

Allows to create a new order with the given data:

```sh
curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders
```
or on windows

```sh
curl -d "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}" -H "Content-Type: application/json" -X POST http://localhost:8080/orders
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

As response a single order is returned if found, otherwise 404 Not Found is returned.

### DELETE /orders/{id}

Cancels order with given id

```sh
curl -X DELETE http://localhost:8080/orders/1
```

### GET /orderItems

Getting order items sub processes

```sh
curl -X GET http://localhost:8080/orderItems
```
Example response:
```json
[
  {
    "id":"66c11e3e-c211-4cee-9a07-848b5e861bc5",
    "order":
    {
      "orderNumber":"12345",
      "shipped":false,
      "total":0.537941914075738
    }
  }
]
```

### GET /usertasks/instance?user={user}

Getting user tasks awaiting user action

```sh
curl -X GET http://localhost:8080/usertasks/instance?user=john
```
Example response:

```json
[
  {
    "id": "1dd3123a-aa2c-4b68-93c3-fd0c2abe76c8",
    "userTaskId": "UserTask_1",
    "status": {
      "terminate": null,
      "name": "Reserved"
    },
    "taskName": "Verify order",
    ...
]
```

### POST /usertasks/instance/{taskId}/transition?user={user}

Complete user task

```sh
curl -d '{"transitionId": "complete"}' -H "Content-Type: application/json" -X POST http://localhost:8080/usertasks/instance/1dd3123a-aa2c-4b68-93c3-fd0c2abe76c8/transition?user=john
```

As response the updated order is returned.

Example response:

```json
{
  "id": "1dd3123a-aa2c-4b68-93c3-fd0c2abe76c8",
  "userTaskId": "UserTask_1",
  "status": {
    "terminate": "COMPLETED",
    "name": "Completed"
  },
  "taskName": "Verify order",
  ...
]
```
