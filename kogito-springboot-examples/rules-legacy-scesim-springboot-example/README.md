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
# RuleUnit + Spring Boot + Test Scenario example

## Description

A simple rule service to validate `Hello` fact and testing it using Scenario Simulation.

An injectable KieRuntimeBuilder is generated, so you can create Drools v7 KieBase and KieSession out of it.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed

### Compile and Run

```sh
mvn clean compile spring-boot:run
```

### Package and Run

```sh
mvn clean package
java -jar target/rules-legacy-scesim-springboot-example.jar
```

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

The [Swagger](http://localhost:8080/swagger-ui/index.html) page shows all the available endpoints, and it could be used to test them.
You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger Editor](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

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
2. set the `kogito.sources.keep` variable to `true`

## Caveat
Requires `org.drools:drools-xml-support` dependency
For the moment being, "globals" are unsupported