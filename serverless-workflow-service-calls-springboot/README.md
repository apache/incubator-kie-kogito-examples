# Kogito Serverless Workflow - Country Info Services Example

## Description

This example showcases the use of [Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/master/workflow/spec) 
markup to create country information
classification workflow. It also showcases how easy it is to create a simple AngularJS app/page 
which you can use to start workflow instances and show the country information classified by the example
workflow.

The serverless workflow used in this example expects a name of a country. It then uses an Operation state
to execute a function service that retrieves country information given the country name from https://restcountries.eu.
It then goes through a Switch state which looks at the provided country population size. The switch state is
very simple with just two conditions, namely if population size is less than 20000000 it classifies it as "Small/Large",
and if greater than 20000000 it classifies it as "Large". The workflow send the information to a classification
service to store the data before it finishes execution.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

### Compile and Run in Local Dev Mode

```text
mvn clean compile spring-boot:run
```

### Package and Run using uberjar

```sh
mvn clean package
```

To run the generated native executable, generated in `target/`, execute

```text
java -jar target/serverless-workflow-service-calls-springboot.jar
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.


### Running the Example

After starting the example application you can access the AngularJS page at:

```text
http://localhost:8080/example/countries.html
```

You should see the following page:

<p align="center">
<img src="img/sw-example1.png" alt="Example1"/>
</p>

Enter in a name of a country in the "Name" text input box (e.g "Germany", "Greece", "USA", etc) and press the 
"Classify" button. 

This will call the serverless workflow passing it the name of the country you entered. The workflow 
will execute and the Country Information portion of the page will refresh showing the classification results, 
for example:

<p align="center">
<img src="img/sw-example2.png" alt="Example2"/>
</p>