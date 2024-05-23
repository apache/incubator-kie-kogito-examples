<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->
# Rules with Legacy API + Quarkus + Test Scenario example

## Description

A simple rule service to validate `Hello` fact and testing it using Scenario Simulation.

An injectable KieRuntimeBuilder is generated, so you can create Drools v7 KieBase and KieSession out of it.

## Installing and Running

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed

When using native image compilation, you will also need:
  - [GraalVM 21.1.0](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-21.1.0) installed
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
./target/rules-legacy-quarkus-example-runner
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

Returns approved Hello from the given fact:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"approved":false,  "greeting":"foo"}' http://localhost:8080/find-approved
```
or on windows

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"approved\":false,  \"greeting\":\"foo\"}" http://localhost:8080/find-approved
```

As response the modified Hello is returned.

Example response:

```json
{"greeting":"foo","approved":true}
```

Returns denied Hello from the given fact:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"approved":false,  "greeting":"bar"}' http://localhost:8080/find-approved
```
or on windows

```sh
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"approved\":false,  \"greeting\":\"bar\"}" http://localhost:8080/find-approved
```

As response the modified Hello is returned.

Example response:

```json
{"greeting":"bar","approved":false}
```
# Test Scenario usage

Test Scenario + rules project created inside Business central should work, with the following requirements:
1. use the pom as defined in the current project

## Caveat
Requires `org.drools:drools-xml-support` dependency
For the moment being, "globals" are unsupported