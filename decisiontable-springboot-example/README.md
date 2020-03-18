# Decision Table + Spring Boot example

## Description

A rule service to validate `LoanApplication` fact. Rules are written as a decision table.

REST endpoints are generated from query rules. You can insert `LoanApplication` facts and query a result via the REST endpoints. Rule resources are assembled as a RuleUnit.

## Installing and Running

### Prerequisites

You will need:
  - Java 1.8.0+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

### Compile and Run

```
mvn clean compile spring-boot:run
```

## Swagger documentation

You can take a look at the [swagger definition](http://localhost:8080/docs/swagger.json) - automatically generated and included in this service (requires `mvn package`) - to determine all available operations exposed by this service.  For easy readability you can visualize the swagger definition file using a swagger UI like for example available [here](https://editor.swagger.io). In addition, various clients to interact with this service can be easily generated using this swagger definition.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /find-approved

Returns approved loan applications from the given facts:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"maxAmount":5000,"loanApplications":[{"id":"ABC10001","amount":2000,"deposit":100,"applicant":{"age":45,"name":"John"}}, {"id":"ABC10002","amount":5000,"deposit":100,"applicant":{"age":25,"name":"Paul"}}, {"id":"ABC10015","amount":1000,"deposit":100,"applicant":{"age":12,"name":"George"}}]}' http://localhost:8080/find-approved
```
or on windows

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"maxAmount\":5000,\"loanApplications\":[{\"id\":\"ABC10001\",\"amount\":2000,\"deposit\":100,\"applicant\":{\"age\":45,\"name\":\"John\"}}, {\"id\":\"ABC10002\",\"amount\":5000,\"deposit\":100,\"applicant\":{\"age\":25,\"name\":\"Paul\"}}, {\"id\":\"ABC10015\",\"amount\":1000,\"deposit\":100,\"applicant\":{\"age\":12,\"name\":\"George\"}}]}" http://localhost:8080/find-approved
```

As response an array of loan applications is returned.

Example response:
```json
[
  {
    "id":"ABC10001",
    "applicant":{
      "name":"John",
      "age":45
    },
    "amount":2000,
    "deposit":100,
    "approved":true
  }
]
```

### POST /find-not-approved-id-and-amount

Returns ids and amount values of rejected loan applications from the given facts:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"maxAmount":5000,"loanApplications":[{"id":"ABC10001","amount":2000,"deposit":100,"applicant":{"age":45,"name":"John"}}, {"id":"ABC10002","amount":5000,"deposit":100,"applicant":{"age":25,"name":"Paul"}}, {"id":"ABC10015","amount":1000,"deposit":100,"applicant":{"age":12,"name":"George"}}]}' http://localhost:8080/find-not-approved-id-and-amount
```

As response an array of loan application ids and amount values is returned.

Example response:
```json
[
  {
    "$amount":5000,
    "$id":"ABC10002"
  },
  {
    "$amount":1000,
    "$id":"ABC10015"
  }
]
```

