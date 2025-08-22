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
# DMN 1.5 + Spring Boot example

## Description

Multiple DMN services to evaluate new features of the DMN 1.5 version

Demonstrates DMN on Kogito capabilities, including REST interface code generation.
It also demonstrates the usage models imported through external jar resources.

In this case, the models are contained in the `org.kie:kie-dmn-test-resources` artifact, and the referenced ones are extracted with the following maven configuration
```xml
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>${dependency-plugin.version}</version>
        <executions>
          <execution>
            <id>unpack</id>
            <phase>generate-resources</phase>
            <goals>
              <goal>unpack</goal>
            </goals>
            <configuration>
              <artifactItems>
                <artifactItem>
                  <groupId>org.kie</groupId>
                  <artifactId>kie-dmn-test-resources</artifactId>
                  <version>${project.version}</version>
                  <classifier>tests</classifier>
                  <type>jar</type>
                  <overWrite>true</overWrite>
                  <outputDirectory>${project.build.directory}/generated-resources</outputDirectory>
                  <includes>valid_models/DMNv1_5/**/AllowedValuesChecksInsideCollection.dmn,
                    valid_models/DMNv1_5/**/TypeConstraintsChecks.dmn,
                    valid_models/DMNv1_5/**/Imported_Model_Unamed.dmn,
                    valid_models/DMNv1_5/**/Importing_EmptyNamed_Model_With_Href_Namespace.dmn
                  </includes>
                </artifactItem>
              </artifactItems>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

Extracted models could be found under `target/generated-resources/valid_models.DMNv1_5` directory.

This example also features the `org.kie.dmn.runtime.typecheck` environment variable, to enforce constraint checks

```xml
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>properties-maven-plugin</artifactId>
        <executions>
          <execution>
            <goals>
              <goal>set-system-properties</goal>
            </goals>
            <configuration>
              <properties>
                <property>
                  <name>org.kie.dmn.runtime.typecheck</name>
                  <value>${enable.runtime.typecheck}</value>
                </property>
              </properties>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

## Installing and Running

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.9+ installed

### Compile and Run

```sh
mvn clean compile spring-boot:run
```

### Package and Run

```sh
mvn clean package
java -jar ./target/dmn-15-springboot-example.jar
```

## Example Usage

Once the service is up and running, multiple services will be available

The [Swagger](http://localhost:8080/swagger-ui/index.html) page should show all the endpoints.


### POST /AllowedValuesChecksInsideCollection

Demonstrates usage of `allowedValues`constraint (to be used as comparison with the `ConstraintsChecks`)

Given inputs:

```json
{
  "p1": {
    "Name": "string",
    "Interests": [
      "Golf"
    ]
  }
}
```

Curl command (using the JSON object above):

```sh
curl -X 'POST' \
  'http://localhost:8080/AllowedValuesChecksInsideCollection' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "p1": {
    "Name": "string",
    "Interests": [
      "Golf"
    ]
  }
}'
```
or on Windows:

```sh
curl -X 'POST' \
  'http://localhost:8080/AllowedValuesChecksInsideCollection' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"p1\": {
    \"Name\": \"string\",
    \"Interests\": [
      \"Golf\"
    ]
  }
}"
```

As response, the interests information is returned.

Example response:

```json
{
  "p1": {
    "Interests": [
      "Golf"
    ],
    "Name": "string"
  },
  "MyDecision": "The Person string likes 1 thing(s)."
}
```



### POST  /TypeConstraintsChecks

Demonstrates usage of `typeConstraint` constraint.

Given inputs:

```json
{
  "p1": {
    "Name": "string",
    "Interests": [
      "anything"
    ]
  }
}
```

Curl command (using the JSON object above):

```sh
curl -X 'POST' \
  'http://localhost:8080/TypeConstraintsChecks' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "p1": {
    "Name": "string",
    "Interests": [
      "anything"
    ]
  }
}'
```
or on Windows:

```sh
curl -X 'POST' \
  'http://localhost:8080/TypeConstraintsChecks' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"p1\": {
    \"Name\": \"string\",
    \"Interests\": [
      \"anything\"
    ]
  }
}"
}"
```

As response, the interests information is returned.

Example response:

```json
{
  "p1": {
    "Interests": [
      "anything"
    ],
    "Name": "string"
  },
  "MyDecision": "The Person string likes 1 thing(s)."
}
```

The following input, on the other side, would rise an error

```json
{
  "p1": {
    "Name": "string",
    "Interests": [
      "string", "strong"
    ]
  }
}
```

Curl command (using the JSON object above):

```sh
curl -X 'POST' \
  'http://localhost:8080/TypeConstraintsChecks' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "p1": {
    "Name": "string",
    "Interests": [
      "string", "strong"
    ]
  }
}'
```
or on Windows:

```sh
curl -X 'POST' \
  'http://localhost:8080/TypeConstraintsChecks' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"p1\": {
    \"Name\": \"string\",
    \"Interests\": [
      \"string\", \"strong\"
    ]
  }
}"
}"
```

### POST  /Imported Model

Used to demonstrates usage of `unnamed` import in the `/Importing empty-named Model` service.

Given inputs:

```json
{
  "A Person": {
    "name": "string",
    "age": 0
  },
  "An Imported Person": {
    "name": "string",
    "age": 0
  }
}
```

Curl command (using the JSON object above):

```sh
curl -X 'POST' \
  'http://localhost:8080/Imported Model' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "A Person": {
    "name": "string",
    "age": 0
  },
  "An Imported Person": {
    "name": "string",
    "age": 0
  }
}'
```
or on Windows:

```sh
curl -X 'POST' \
  'http://localhost:8080/Imported Model' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"A Person\": {
    \"name\": \"string\",
    \"age\": 0
  },
  \"An Imported Person\": {
    \"name\": \"string\",
    \"age\": 0
  }
}"
```

As response, the greeting is returned.

Example response:

```json
{
  "A Person": {
    "name": "string",
    "age": 0
  },
  "Say Hello": "function Say Hello( Person )",
  "Remote Greeting": "Hello string!",
  "An Imported Person": {
    "name": "string",
    "age": 0
  }
}
```

### POST  /Importing empty-named Model

Used to demonstrates usage of `unnamed` import (it refers to the dmn model behind the `/Imported Model` service).

Given inputs:

```json
{
  "A Person": {
    "name": "string",
    "age": 0
  },
  "An Imported Person": {
    "name": "string",
    "age": 0
  }
}
```

Curl command (using the JSON object above):

```sh
curl -X 'POST' \
  'http://localhost:8080/Importing empty-named Model' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "A Person": {
    "name": "string",
    "age": 0
  },
  "An Imported Person": {
    "name": "string",
    "age": 0
  }
}'
```
or on Windows:

```sh
curl -X 'POST' \
  'http://localhost:8080/Importing empty-named Model' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d "{
  \"A Person\": {
    \"name\": \"string\",
    \"age\": 0
  },
  \"An Imported Person\": {
    \"name\": \"string\",
    \"age\": 0
  }
}"
```

As response, both model-local and imported greetings are returned.

Example response:

```json
{
  "Local Hello": "function Local Hello( Person )",
  "A Person": {
    "name": "string",
    "age": 0
  },
  "Say Hello": "function Say Hello( Person )",
  "Imported Greeting": "Hello string!",
  "Local Greeting": "Local Hello string!",
  "An Imported Person": {
    "name": "string",
    "age": 0
  }
}
```

