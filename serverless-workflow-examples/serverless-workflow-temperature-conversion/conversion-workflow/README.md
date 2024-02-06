# Temperature Conversion Service

## Description

This example contains a workflow that performs two consecutive REST invocations defined as functions.  
The workflow is described using JSON format as defined in the
[CNCF Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/main/workflow/spec).

The workflow expects a JSON input containing the temperature in Fahrenheits:

```json
{
  "fahrenheit": 100
}
```

The workflow starts defining the constants to be used during the computation.
Then it will call a sequence of REST functions to solve the equation: `Celsius = (Fahrenheit - 32) * 0.5553`.
Finally, the result will be returned to the caller, the final product of the equation is the temperature converted to Celsius.

## Installing and Running

### Prerequisites

You will need:

- Java 17+ installed
- Environment variable `JAVA_HOME` set accordingly
- Maven 3.9.6+ installed

When using native image compilation, you will also need:

- [GraalVm](https://www.graalvm.org/downloads/) 20.2.0+ installed
- Environment variable `GRAALVM_HOME` set accordingly
- Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```bash
mvn clean package quarkus:dev
```

### Compile and Run in JVM mode

```bash
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

or on Windows

```bash
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Compile and Run using Local Native Image

Note that this requires `GRAALVM_HOME` to point to a valid GraalVM installation

```bash
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```bash
./target/conversion-workflow-runner
```

### Submit a request

Before submitting a request, run the [Subtraction](../subtraction-service) and [Multiplication](../multiplication-service) on separate terminals
on ports `8181` and `8282` respectively (use the property `quarkus.http.port`).

The service based on the JSON workflow definition can be access by sending a POST request to [http://localhost:8080/fahrenheit_to_celsius](http://localhost:8080/fahrenheit_to_celsius) with the following content:

```json
{
  "workflowdata": {
    "fahrenheit": 100
  }
}
```

Complete curl command can be found below:

```bash
curl -X POST \
    -H 'Content-Type:application/json' \
    -H 'Accept:application/json' \
    -d '{"fahrenheit": 100}' \
    http://localhost:8080/fahrenheit_to_celsius | jq .
```

You should have a reply similar to this one:

```json
{
  "id": "2287167f-1392-480e-8e20-6acd5922dfac",
  "workflowdata": {
    "fahrenheit": 100,
    "subtractValue": "32.0",
    "multiplyValue": "0.5556",
    "difference": 68.0,
    "product": 37.7808
    }
  }
}
```

## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
