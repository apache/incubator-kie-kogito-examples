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
  - [GraalVM 19.1.1](https://github.com/oracle/graal/releases/tag/vm-19.1.1) installed 
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```
mvn clean package quarkus:dev    
```

### Compile and Run in JVM mode

```
mvn clean package 
java -jar target/jbpm-quarkus-example-{version}-runner.jar    
```

or on windows

```
mvn clean package
java -jar target\jbpm-quarkus-example-{version}-runner.jar
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

Note: This does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon.

### Running with persistence enabled

Kogito supports runtime persistence that is backed by Infinispan. So to be able to enable this you need to have 
Infinispan server installed and available over the network. By default it expects it to be at (it can be configured via application.properties file located in src/main/resources)

```
quarkus.infinispan-client.server-list=localhost:11222
```

You can install Infinispan server by downloading it from [Infinispan website](https://infinispan.org/download/), you should use version 10.0.x.  To enable our simplified demo setup, go to /server/conf/infinispan.xml and remove the security domain from the endpoints definition:

```
<endpoints socket-binding="default">
```

Once Infinispan is up and running you can build this project with `-Ppersistence` to enable additional processing during the build. Next you start it in exact same way as without persistence.

This extra profile in maven configuration adds additional dependencies needed to work with Infinispan as persistent store. 


## Swagger documentation

You can take a look at the [swagger definition](http://localhost:8080/docs/swagger.json) - automatically generated and included in this service - to determine all available operations exposed by this service.  For easy readability you can visualize the swagger definition file using a swagger UI like for example available [here](https://editor.swagger.io). In addition, various clients to interact with this service can be easily generated using this swagger definition.

When running in Quarkus development mode, we also leverage the Quarkus openapi extension that exposes [swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests. 

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.  Note that rather than using the curl commands below, you can also use the [swagger UI](http://localhost:8080/swagger-ui/) to send requests.

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

Example response:
```json
    {
      "approver": "john",
      "id": "b5225020-4cf4-4e91-8f86-dc840589cc22",
      "order": {
        "orderNumber": "12345",
        "shipped": false,
        "total": 0.529655982561999
      }
    }
```

### GET /orders

Returns list of orders currently active:

```sh
curl -X GET http://localhost:8080/orders
```
Example response:
```json
    [{
      "approver": "john",
      "id": "b5225020-4cf4-4e91-8f86-dc840589cc22",
      "order": {
        "orderNumber": "12345",
        "shipped": false,
        "total": 0.529655982561999
      }
    }]
```

As response an array of orders is returned.

### GET /orders/{id}

Returns order with given id (if active):

```sh
curl -X GET http://localhost:8080/orders/b5225020-4cf4-4e91-8f86-dc840589cc22
```
Example response:
```json
    {
      "approver": "john",
      "id": "b5225020-4cf4-4e91-8f86-dc840589cc22",
      "order": {
        "orderNumber": "12345",
        "shipped": false,
        "total": 0.529655982561999
      }
    }
```

As response a single order is returned if found, otherwise no content (204) is returned.

### DELETE /orders/{id}

Cancels order with given id

```sh
curl -X DELETE http://localhost:8080/orders/b5225020-4cf4-4e91-8f86-dc840589cc22
```
Example response:
```json
    {
      "approver": "john",
      "id": "b5225020-4cf4-4e91-8f86-dc840589cc22",
      "order": {
        "orderNumber": "12345",
        "shipped": false,
        "total": 0.529655982561999
      }
    }
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

### GET /orderItems/{id}/tasks

Getting user tasks awaiting user action

```sh
curl -X GET http://localhost:8080/orderItems/66c11e3e-c211-4cee-9a07-848b5e861bc5/tasks
```
Example response:
```json
{
  "62f1c985-d31c-4ead-9906-2fe8d05937f0":"Verify order"
}
```

### GET /orderItems/{id}/Verify_order/{tid}

Getting user task details

```sh
curl -X GET http://localhost:8080/orderItems/66c11e3e-c211-4cee-9a07-848b5e861bc5/Verify_order/62f1c985-d31c-4ead-9906-2fe8d05937f0
```
Example response:
```json
{
  "id":"62f1c985-d31c-4ead-9906-2fe8d05937f0",
  "input1":
  {
    "orderNumber":"12345",
    "shipped":false,
    "total":0.537941914075738
  },
  "name":"Verify order"
}
```

### POST /orderItems/{id}/Verify_order/{tid}

Complete user task

```sh
curl -d '{}' -H "Content-Type: application/json" -X POST http://localhost:8080/orderItems/66c11e3e-c211-4cee-9a07-848b5e861bc5/Verify_order/62f1c985-d31c-4ead-9906-2fe8d05937f0
```


As response the updated order is returned.

Example response:
```json
{
  "id":"66c11e3e-c211-4cee-9a07-848b5e861bc5",
  "order":
  {
    "orderNumber":"12345",
    "shipped":false,
    "total":0.537941914075738
  }
}
```