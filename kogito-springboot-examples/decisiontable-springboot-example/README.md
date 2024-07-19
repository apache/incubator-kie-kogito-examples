# Decision Table + Spring Boot example

| | |
|-|-|
|`NOTE`|**Looking for modern decision tables?**<br />Kogito, like Drools, offers support of the [DMN open standard](https://drools.org/learn/dmn.html) for visual and semantic execution of declarative logic. The business rules in this example may be also expressed using DMN decision tables (or other visual paradigm of DMN), instead of Drools XLS decision tables.<br/>For more information about DMN support in Kogito, you may refer to the [Kogito DMN documentation](https://docs.kogito.kie.org/latest/html_single/#_using_dmn_models_in_kogito_services).|

## Description

A rule service to validate `LoanApplication` fact. Rules are written as a decision table.

REST endpoints are generated from query rules. You can insert `LoanApplication` facts and query a result via the REST endpoints. Rule resources are assembled as a RuleUnit.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

### Compile and Run

```sh
mvn clean compile spring-boot:run
```

### Package and Run

```sh
mvn clean package
java -jar target/decisiontable-springboot-example.jar
```

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /find-approved

Returns approved loan applications from the given facts:

Given facts:

```json
{
    "maxAmount":5000,
    "loanApplications":[
        {
            "id":"ABC10001",
            "amount":2000,
            "deposit":100,
            "applicant":{"age":45,"name":"John"}
        },
        {
            "id":"ABC10002",
            "amount":5000,
            "deposit":100,
            "applicant":{"age":25,"name":"Paul"}
        },
        {
            "id":"ABC10015",
            "amount":1000,
            "deposit":100,
            "applicant":{"age":12,"name":"George"}
        }
    ]
}
```

Example curl request (using the JSON above):

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

Example curl request (using the JSON from previous example):

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
