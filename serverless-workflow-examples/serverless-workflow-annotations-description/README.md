# Kogito Serverless Workflow - Annotations and Description Example

## Description

This example contains two simple workflow services containing the `annotation` and `description` attributes. 
The services are described using both JSON and YAML formats as defined in the 
[CNCF Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/main/workflow/spec).

These workflow definitions will generate an Open API document like the following (note the tags "Cogito", "ergo", "sum", "jsonannotations", and "yamlannotations"):

````yaml
---
openapi: 3.0.3
info:
  title: serverless-workflow-annotations API
tags:
  - name: Cogito
  - name: ergo
  - name: jsonannotations
    description: This is an amazing workflow
  - name: sum
  - name: yamlannotations
    description: This is an amazing workflow
paths:
  /:
    post:
      requestBody:
        content:
          '*/*':
            schema:
              $ref: '#/components/schemas/CloudEvent'
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Response'
# more paths here
````

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```

### Compile and Run in JVM mode

```sh
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```sh
./target/sw-quarkus-greeting-{version}-runner
```

### Download the Open API document from

```
http://localhost:8080/q/openapi
```