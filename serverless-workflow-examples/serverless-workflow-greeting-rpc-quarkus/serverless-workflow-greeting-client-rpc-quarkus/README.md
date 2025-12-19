# Kogito Serverless Workflow - Greeting Example

## Description

This example contains four simple greeting workflow services that use gRPC and one workflow for the purpose of error testing.
The services are described using a JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/main/workflow/spec).

The workflows are: 
1. jsongreet - Simple gRPC. The service sends a name and a language as input and receives one greeting of a person with that name in the specified language.
2. jsongreetserverstream - Server-side streaming gRPC. The service sends a name and the gRPC server streams greetings in all supported languages.
3. jsongreetclientstream - Client-side streaming gRPC. The service streams name and language pairs as inputs and receives corresponding greetings as one block of text after the streaming is finished.
4. jsongreetbidistream - Bidirectional streaming gRPC. The service streams name and language pairs as inputs and the gRPC server continually streams back respective greetings.
5. jsongreetbidistreamerror - Same as previous, but this use case simulates an error thrown by the gRPC server during streaming. 

Each workflow expects a different JSON input based on the gRPC method called.
(see details in the [Submit a request](#Submit-a-request) section).

Each flow then prints out the greeting(s) to the console.

The languages supported currently are English and Spanish. In case a supported language is not recognized, English is chosen as a default.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed

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

### Submit a request

#### Simple gRPC

The service based on the JSON workflow definition can be accessed by sending a request to http://localhost:8080/jsongreet
with the following content 

```json
{
  "name": "John",
  "language": "English"
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name": "John", "language": "English"}' http://localhost:8080/jsongreet
```

Log after curl executed:

```json
{"id":"541a5363-1667-4f6d-a8b4-1299eba81eac","workflowdata":{"name":"John","language":"English","message":"Hello from gRPC service John"}}
```

If you would like to greet the person in Spanish, we need to pass the following data on workflow start:

```json
{
  "name": "John",
  "language": "Spanish"
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{{"name": "John", "language": "Spanish"}' http://localhost:8080/jsongreet
```

#### Server-side streaming gRPC

The service based on the JSON workflow definition can be accessed by sending a request to http://localhost:8080/jsongreetserverstream
with the following content

```json
{
  "name": "John"
}
```

Language parameter is not needed as the gRPC server will send greetings in all languages at all times.

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name": "John"}' http://localhost:8080/jsongreetserverstream
```

Log after curl executed:

```json
{"id":"665911c5-36ee-40b7-93dd-a2328f969c73","workflowdata":{"name":"John","response":[{"message":"Hello from gRPC service John"},{"message":"Saludos desde gRPC service John"}]}}
```

Notice that greetings in both languages were received.

#### Client-side streaming gRPC

The service based on the JSON workflow definition can be accessed by sending a request to http://localhost:8080/jsongreetclientstream
with the following content

```json
{
  "helloRequests": [
    {
      "name": "Javierito",
      "language": "Spanish"
    },
    {
      "name": "John",
      "language": "English"
    },
    {
      "name": "Jan",
      "language": "Czech"
    }
  ]
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"helloRequests" : [{"name" : "Javierito", "language":"Spanish"}, {"name" : "John", "language":"English"}, {"name" : "Jan", "language":"Czech"} ]}' http://localhost:8080/jsongreetclientstream
```

Log after curl executed:

```json
{"id":"abece3f9-0797-4c10-a1f5-8f3929724689","workflowdata":{"helloRequests":[{"name":"Javierito","language":"Spanish"},{"name":"John","language":"English"},{"name":"Jan","language":"Czech"}],"message":"Saludos desde gRPC service Javierito\nHello from gRPC service John\nHello from gRPC service Jan"}}
```

Notice that one greeting with respective names and languages was received.

#### Bidirectional streaming gRPC

The service based on the JSON workflow definition can be accessed by sending a request to http://localhost:8080/jsongreetbidistream
with the following content

```json
{
  "helloRequests": [
    {
      "name": "Javierito",
      "language": "Spanish"
    },
    {
      "name": "John",
      "language": "English"
    },
    {
      "name": "Jan",
      "language": "Czech"
    }
  ]
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"helloRequests" : [{"name" : "Javierito", "language":"Spanish"}, {"name" : "John", "language":"English"}, {"name" : "Jan", "language":"Czech"}]}' http://localhost:8080/jsongreetbidistream
```

Log after curl executed:

```json
{"id":"403876ed-0db4-40ca-a19c-158f563fef16","workflowdata":{"helloRequests":[{"name":"Javierito","language":"Spanish"},{"name":"John","language":"English"},{"name":"Jan","language":"Czech"}],"response":[{"message":"Saludos desde gRPC service Javierito"},{"message":"Hello from gRPC service John"},{"message":"Hello from gRPC service Jan"}]}}
```

Notice that this time individual corresponding greetings were received.

#### Error while streaming gRPC

The service based on the JSON workflow definition can be accessed by sending a request to http://localhost:8080/jsongreetbidistreamerror
with the same content as the bidirectional scenario. Log after curl executed is:

```json
{"failedNodeId":"_jbpm-unique-4","id":"edcb844c-87db-4660-af17-ebe1cc853e0a","message":"io.grpc.StatusRuntimeException - OUT_OF_RANGE"}
```
