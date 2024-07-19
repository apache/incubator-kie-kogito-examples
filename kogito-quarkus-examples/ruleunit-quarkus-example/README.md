# RuleUnit + Quarkus example

## Description

A simple rule service to validate `LoanApplication` fact.

REST endpoints are generated from query rules. You can insert `LoanApplication` facts and query a result via the REST endpoints. Rule resources are assembled as a RuleUnit.

## Installing and Running

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed

When using native image compilation, you will also need:
  - [GraalVM 19.2.1](https://github.com/oracle/graal/releases/tag/vm-19.2.1) installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```sh
./target/ruleunit-quarkus-example-runner
```

Note: This does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon.

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.  Note that rather than using the curl commands below, you can also use the [swagger UI](http://localhost:8080/swagger-ui/) to send requests.

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
