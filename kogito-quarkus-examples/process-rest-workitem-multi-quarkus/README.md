# Kogito Process - Rest Example

## Description

This example contains a BPMN that performs two consecutive REST invocations using [RestWorkItemHandler](https://github.com/kiegroup/kogito-runtimes/blob/main/kogito-workitems/kogito-rest-workitem/src/main/java/org/kogito/workitem/rest/RestWorkItemHandler.java), an alternative declarative approach to service programatic calls. 

Note that in order to user a WorkItem in Kogito Editor, corresponding .wid file needs to located together with the bpmn file under the same directory

The BPMN expects a JSON input containing a collections of numbers.

The process starts invoking a GET to obtain a random integer. 
This integer is passed together with the list of numbers to  a second REST invocation, a POST, which multiply each element of the array by the generated number
and returns the sum. 

## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

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

The process can be executed by sending a request to [http://localhost:8080/RestExample](http://localhost:8080/RestExample)
with following content 

```json
{
  "inputNumbers": {
   "numbers": [
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
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"inputNumbers" : {"numbers": [1,2,3,4,5,6,7,8,7]}}' http://localhost:8080/RestExample
```

curl response will be something like this, which includes field `sum`, the result of multiplying each input number by 8 (that number might differ in your execution) and summing all of them:

```text
{"id":"8e79ac60-c0c1-40d0-808e-8d3585307661","randomNumber":8,"sum":344,"inputNumbers":{"numbers":[1,2,3,4,5,6,7,8,7]}}
```


```
## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
