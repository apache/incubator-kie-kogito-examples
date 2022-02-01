# Kogito Serverless Workflow - Greeting Example

## Description

This example contains two simple greeting workflow services. 
The services are described using both JSON and YAML formats as defined in the 
[CNCF Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/main/workflow/spec).

The workflow expects as JSON input containing the name of the person to greet, and the language in 
which to greet them in
(see details in the [Submit a request](#Submit-a-request) section).

The workflow starts with a SWITCH state, which is like a gateway. The switch state 
decides which language to greet the person in based on the workflow input "language" parameter.
Depending on the language the workflow then injects the language-based greeting via RELAY states.
Relay states are just "pass" states which do not execute any functions and only have the ability
to inject data into the workflow.
The inject states then transition to the OPERATION state which call a "sysout" function passing it 
input parameter containing the greeting and the name of the person to greet: "$.greeting $.name".
The function then prints out the greeting to the console.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.1+ installed

### Compile and Run in Local Dev Mode

```sh
mvn clean compile spring-boot:run
```

### Package and Run using uberjar

```sh
mvn clean package
```

To run the generated native executable, generated in `target/`, execute

```sh
java -jar target/serverless-workflow-greeting-springboot.jar
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/jsongreet'
with following content 

```json
{
  "workflowdata": {
   "name" : "John",
   "language": "English"
  }
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "English"}}' http://localhost:8080/jsongreet
```

Log after curl executed:

```json
{"id":"541a5363-1667-4f6d-a8b4-1299eba81eac","workflowdata":{"name":"John","language":"English","greeting":"Hello from JSON Workflow, "}}
```

In Spring Boot you should see the log message printed:

```text
Hello from JSON Workflow, John
```

If you would like to greet the person in Spanish, we need to pass the following data on workflow start:

```json
{
  "workflowdata": {
   "name" : "John",
   "language": "Spanish"
  }
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "Spanish"}}' http://localhost:8080/jsongreet
```

In Spring Boot you should now see the log message printed: 

```text
Saludos desde JSON Workflow, John
```

Similarly the service based on the YAML workflow definition can be access by sending a request to http://localhost:8080/yamlgreet'
using the same content:

```json
{
  "workflowdata": {
   "name" : "John",
   "language": "English"
  }
}
``` 

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "English"}}' http://localhost:8080/yamlgreet
```
 
In Spring Boot you should see the log message:

```text
Hello from YAML Workflow, John
```

You can also change the language parameter value to "Spanish" to get the greeting in Spanish.