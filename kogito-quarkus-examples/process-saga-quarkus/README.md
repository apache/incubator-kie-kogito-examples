# Saga Process example with Quarkus

## Description

Service to demonstrate how to implement Saga pattern based on BPMN process with Kogito. The proposed example is based
 on an Order Fulfillment process which consists in a sequence of steps, that could represent calls to external
  services, microservices, serverless functions, etc.
  
 All steps `stock`, `payment` and `shipping` should be executed to confirm an Order, if any of the
  steps fail, then a compensation for each completed step should be executed to undo the operation or to keep the
   process on a consistent state. For instance, reserve stock step, should cancel the stock reservation. The
    compensations for the steps are represented in the process using a boundary `Intermediate Catching Compensation
Event` attached to the respective step to be compensated.          

<img src="docs/images/boundary-compensation.png" height="150px"/>

The catching compensation events can be triggered by an `Intermediate Throwing Compensation Event` or the
 ` Compensation End Event` in any point of the process that represents an error or inconsistent state, like a response
  from a service.
 
 <img src="docs/images/throwing-compensation.png" height="100px"/>

The steps and compensations actions in the process example are implemented as service tasks using a Java class under
 the `src` of the project, and for this example they are just mocking responses, but in a real use case they
  could be executing calls to external services through REST, or any other mechanism depending on the architecture. 
 
 The start point of Saga process is to submit a request to create a new Order with a given `orderId`, this could be
  any other payload that represents an `Order`, but for the sake of simplicity, in this example it will be
   based on the `id` that could be used as a correlation to client starting the Saga.
  The output of each step, is represented by a `Response` that contains a type, indicating <b>success</b> or <b>error
  </b> and the id of the resource that was invoked in the service, but this could be any kind of response depending on
   the requirement of each service.

## Order Saga process

This is the BPMN process that represents the Order Saga, and it is the one being used in the project to be built using
 kogito.

<img src="docs/images/orders-saga-svg.svg" width="80%"/>

## Installing and Running

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

When using native image compilation, you will also need:
  - [GraalVM 19.1.1](https://github.com/oracle/graal/releases/tag/vm-19.1.1) installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/process-saga-quarkus-runner.jar
```

### Package and Run using Local Native Image
Note that the following configuration property needs to be added to `application.properties` in order to enable automatic registration of `META-INF/services` entries required by the workflow engine:
```
quarkus.native.auto-service-loader-registration=true
```

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/process-saga-quarkus-runner
```

Note: Native builds does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon.

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

## Usage

Once the service is up and running, you can use the following examples to interact with the service. Note that rather than using the curl commands below, you can also use the [Swagger UI](http://localhost:8080/swagger-ui/) to send requests.

### Starting the Order Saga

#### POST /order

Allows to start a new Order Saga with the given data:

Given data:

```json
{
    "orderId" : "03e6cf79-3301-434b-b5e1-d6899b5639aa"
    
}
```

Curl command (using the JSON object above):

```sh
curl -H "Content-Type: application/json" -X POST http://localhost:8080/order -d '{"orderId" : "03e6cf79-3301-434b-b5e1-d6899b5639aa"}'
```
The response for the request is returned with attributes representing the response of each step, either
 success or failure. The `orderResponse` attribute indicates if the order can be confirmed in case of success or
  canceled in case of error.

Response example:

```json
    {
    "id": "799742b9-2903-45a3-be96-e7798f9425eb",
    "stockResponse": {
        "type": "SUCCESS",
        "resourceId": "c408e18d-6ec7-48bf-8396-ef2d45ab53d5"
    },
    "paymentResponse": {
        "type": "SUCCESS",
        "resourceId": "700672de-b897-4db6-b45e-411e2ade83a0"
    },
    "orderId": "12345678",
    "failService": "",
    "orderResponse": {
        "type": "SUCCESS",
        "resourceId": "12345678"
    },
    "shippingResponse": {
        "type": "SUCCESS",
        "resourceId": "3c3b8324-7e58-45ca-a939-244c19002e36"
    }
}
```

In the console executing the application you can check the log it with the executed steps.

```text
17:16:58:864 INFO  [org.kie.kogito.examples.StockService] Created Stock for 12345678 with Id: 8ab1ac13-38d0-49e6-ab40-1edd2dc39922
17:16:58:865 INFO  [org.kie.kogito.examples.PaymentService] Created Payment for 12345678 with Id: 2bfc044a-ccb4-4072-a26e-5a533d835257
17:16:58:865 INFO  [org.kie.kogito.examples.ShippingService] Created Shipping for 12345678 with Id: 84a45015-c98b-4e08-b4cd-cf05a19b87e1
17:16:58:865 INFO  [org.kie.kogito.examples.OrderService] Success Order 12345678
```

#### Simulating errors to activate the compensation flows

To make testing the process easier it was introduced an optional attribute `failService` that indicates which service
 should respond with an error. The attribute is basically the simple class name of the service.

Example:

```json
{
    "orderId" : "03e6cf79-3301-434b-b5e1-d6899b5639aa",
    "failService" : "PaymentService"    
}
```
Curl command (using the JSON object above):

```sh
curl -H "Content-Type: application/json" -X POST http://localhost:8080/order -d '{"orderId" : "03e6cf79-3301-434b-b5e1-d6899b5639aa", "failService" : "PaymentService"}' 
```

Response example:

```json
{
    "id": "ef9c8b05-381c-456d-bf43-fbf5331a5e29",
    "stockResponse": {
        "type": "SUCCESS",
        "resourceId": "9098daa2-f40f-4231-995a-1c7d159df190"
    },
    "paymentResponse": {
        "type": "SUCCESS",
        "resourceId": "d6ac4086-efe9-4a9e-849c-2b6d48dbc1f0"
    },
    "orderId": "12345678",
    "failService": "ShippingService",
    "orderResponse": {
        "type": "ERROR",
        "resourceId": "12345678"
    },
    "shippingResponse": {
        "type": "ERROR",
        "resourceId": "39c40aa1-10af-42ad-8ba2-b8dd9c6279e1"
    }
}
```

In the console executing the application you can check the log it with the executed steps.

```text
17:16:17:723 INFO  [org.kie.kogito.examples.StockService] Created Stock for 12345678 with Id: 9098daa2-f40f-4231-995a-1c7d159df190
17:16:17:724 INFO  [org.kie.kogito.examples.PaymentService] Created Payment for 12345678 with Id: d6ac4086-efe9-4a9e-849c-2b6d48dbc1f0
17:16:17:724 INFO  [org.kie.kogito.examples.ShippingService] Created Shipping for 12345678 with Id: 39c40aa1-10af-42ad-8ba2-b8dd9c6279e1
17:16:17:746 WARN  [org.kie.kogito.examples.ShippingService] Cancel Shipping for 39c40aa1-10af-42ad-8ba2-b8dd9c6279e1
17:16:17:746 WARN  [org.kie.kogito.examples.PaymentService] Cancel Payment for d6ac4086-efe9-4a9e-849c-2b6d48dbc1f0
17:16:17:747 WARN  [org.kie.kogito.examples.StockService] Cancel Stock for 9098daa2-f40f-4231-995a-1c7d159df190
17:16:17:747 WARN  [org.kie.kogito.examples.OrderService] Failed Order 12345678
```
