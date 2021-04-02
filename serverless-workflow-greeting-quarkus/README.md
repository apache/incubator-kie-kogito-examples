# Kogito Serverless Workflow - Greeting Example

## Description

This example contains two simple greeting workflow services. 
The services are described using both JSON and YAML formats as defined in the 
[CNCF Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/master/workflow/spec).

The workflow expects as JSON input containing the name of the person to greet, and the language in 
which to greet them in
(see details in the [Submit a request](#Submit-a-request) section).

The workflow starts with a SWICH state, which is like a gateway. The switch state 
decides which language to greet the person in based on the workflow input "language" parameter.
Depending on the language the workflow then injects the language-based greeting via RELAY states.
Relay states are just "pass" states which do no execute any functions and only have the ability
to inject data into the workflow.
The inject states then transition to the OPERATION state which call a "sysout" function passing it 
input parameter containing the greeting and the name of the person to greet: "$.greeting $.name".
The function then prints out the greeting to the console.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```text
mvn clean package quarkus:dev    
```

### Compile and Run in JVM mode

```text
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```text
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```text
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```text
./target/sw-quarkus-greeting-{version}-runner
```

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

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "English"}}' http://localhost:8080/jsongreet
```

Log after curl executed:

```text
{"id":"541a5363-1667-4f6d-a8b4-1299eba81eac","workflowdata":{"name":"John","language":"English","greeting":"Hello from JSON Workflow, "}}
```

In Quarkus you should see the log message printed:

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

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "Spanish"}}' http://localhost:8080/jsongreet
```

In Quarkus you should now see the log message printed: 

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

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "John", "language": "English"}}' http://localhost:8080/yamlgreet
```
 
In Quarkus you should see the log message:

```text
Hello from YAML Workflow, John
```

You can also change the language parameter value to "Spanish" to get the greeting in Spanish.

## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
