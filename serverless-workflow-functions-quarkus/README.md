# Kogito Serverless Workflow - Rest Example

## Description

This example contains a workflow that performs two consecutive REST invocations defined as functions.  
The workflow is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/master/workflow/spec).

The workflow expects a JSON input containing a collections of numbers.

The workflow starts invoking a GET to obtain a random integer. 
This integer is passed together with the list of numbers to  a second REST invocation, a POST, which multiply each element of the array by the generated number
and returns the sum. 
Finally, the resulting integer is printed using sysout script. 

## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 20.2.0+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```text
mvn clean package quarkus:dev    
```

### Compile and Run in JVM mode

```text
mvn clean package 
java -jar target/sw-quarkus-greeting-{version}-runner.jar    
```

or on windows

```text
mvn clean package
java -jar target\sw-quarkus-greeting-{version}-runner.jar
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

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/RESTExample'
with following content 

```json
{
  "workflowdata": {
   "inputNumbers": [
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            7
        ]
  }
}
```

Complete curl command can be found below:

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"inputNumbers": [1,2,3,4,5,6,7,8,7]]}}' http://localhost:8080/RESTExample
```

Log after curl executed:

```text
{
    "workflowdata": {
        "inputNumbers": [
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            7
        ],
    }
}

```

In Quarkus you should see the log message printed:

```text
The sum is: 387
```
## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
