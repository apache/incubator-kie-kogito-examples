# jBPM + SpringBoot example

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

## Installing and Running

```
mvn clean package spring-boot:run    
```

### Running with persistence enabled

Kogito support runtime persistence that is backed by Inifinispan. So to be able to enable this you need to have 
Infinispan server installed and available over the network. By default it expects it to be at

```
localhost:11222
```

it can be configured via application.properties file located in src/main/resources

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