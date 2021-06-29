# Process with DMN Business Rules

## Description

A quickstart project that shows the use of business rules and processes

This example shows

* make use of DMN to define rules
* make use of business rules task in the process to evaluate rules


### Garbage Fee Domain Details
We prepared demo application for paying garbage fee. The fee is computed according to the number of people in the household and their residence. The fee computation logic is defined in the 'garbage fee.dmn' file.

Next, we prepared 'pay garbage fee.bpmn' process with a 'fee' variable. Once process instance is finished, that variable says what is the fee amount needed to pay.


## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

### Compile and Run in Local Dev Mode

```
mvn clean compile spring-boot:run
```


### Package and Run using uberjar

```
mvn clean package
```

To run the generated native executable, generated in `target/`, execute

```
java -jar target/process-business-rules-dmn-springboot.jar
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.


## Example Usage

Once the service is up and running we can invoke the REST endpoints and examine the logic.

### Submit a request

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/paygarbagefee`  with appropriate contents. See the following two cases:

#### Temporal residence for two in London

Given data:

```json
{
  "peopleCount": 2,
  "residence": {
    "city": "London",
    "type": "temporal"
  }
}
```

Submit the JSON object from above:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"peopleCount": 2, "residence" : {"city" : "London", "type" : "temporal"}}' http://localhost:8080/paygarbagefee
```

After the Curl command you should see a similar console log

```json
{
    "TODO" : "KOGITO-5366"
}
```

## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
