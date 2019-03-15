# jBPM + SpringBoot example

## Installing and Running

-   Prerequisites: build locally submarine-bom and submarine-runtimes

*   Compile and Run

    ```
    mvn clean package spring-boot:run
    ```

## Swagger documentation

Point to [swagger docs](http://localhost:8080/docs/swagger.json) to retrieve swagger definition of the exposed service

You can visualize that JSON file at [swagger editor](https://editor.swagger.io)

In addition client application can be easily generated from the swagger definition to interact with this service.

## Examples

### post /orders

Allows to create new orders with following sample command

```sh
curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" \
    -X POST http://localhost:8080/orders
```

As response the updated order is returned.

### get /orders

Returns list of orders currently active with following command

```sh
curl -H "Accept: application/json" -X GET http://localhost:8080/orders
```

As response array of orders is returned

### get /orders/{id}

Returns specified order active following command

```sh
curl -H "Accept: application/json" -X GET http://localhost:8080/orders/1
```

As response single order is returned if found otherwise no content (204)

### delete /orders/{id}

Cancels specified order with following command

```sh
curl -H "Accept: application/json" -X DELETE http://localhost:8080/orders/1
```
