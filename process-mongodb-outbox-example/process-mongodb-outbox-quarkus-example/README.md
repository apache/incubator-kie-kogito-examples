# Process + Quarkus

## Installing and Running

### Compile and Run in Local Dev Mode

```
mvn clean compile quarkus:dev
```

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service. Note that rather than using the curl commands below, you can also use the [Swagger UI](http://localhost:8080/swagger-ui/) to send requests.

### POST /orders

Allows to create a new order with the given data:

Given data:

```json
{
    "approver" : "john",
    "order" : {
        "orderNumber" : "12345",
        "shipped" : false
    }
}
```

Curl command (using the JSON object above):

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

As response a single order is returned if found, otherwise 404 Not Found is returned.

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
curl -X GET http://localhost:8080/orderItems/66c11e3e-c211-4cee-9a07-848b5e861bc5/tasks?user=john
```
Example response:

```json
[
  {"id":"62f1c985-d31c-4ead-9906-2fe8d05937f0","name":"Verify order"}
]
```

### GET /orderItems/{id}/Verify_order/{tid}

Getting user task details

```sh
curl -X GET http://localhost:8080/orderItems/66c11e3e-c211-4cee-9a07-848b5e861bc5/Verify_order/62f1c985-d31c-4ead-9906-2fe8d05937f0?user=john
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
curl -d '{}' -H "Content-Type: application/json" -X POST http://localhost:8080/orderItems/66c11e3e-c211-4cee-9a07-848b5e861bc5/Verify_order/62f1c985-d31c-4ead-9906-2fe8d05937f0?user=john
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
