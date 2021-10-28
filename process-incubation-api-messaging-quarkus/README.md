# Process Incubation API

## Description

This quickstart project demonstrate how to use the Kogito Public API (*Incubation*) with Messaging and Kafka. It disables the predefined generated REST endpoint and instead uses the Public API to interact with the process.

A message consumer receives a message and evaluates a process using the process id and publish a response.

- when a new message is received in the topic ´hello´, it starts a process that prints a "hello" message to console and publishes a response in another topic `hello-response`.
- the quickstart uses the public API to define a message consumer/publisher instead of any kogito messaging addon.

*Incubation* means that this API is experimental, but it is part of a regular release for early access and to gather community feedback.

All configuration details like the Kafka broker URL and topic names can be found in  [applications.properties](src/main/resources/application.properties) file.

## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven installed

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Submit a request

To make use of this application it is as simple as sending a message to the configured topic ´hello´ with the following content

```json
{
   "user" : {   
       "firstName": "Marty",
       "lastName" : "McFly"
   }
}

```

Response is sent in the topic ´hello-response´, and it should be similar to:

```json
{
    "user": {
        "firstName": "Marty",
        "lastName": "McFly"
    },
    "greetings": "Hello, Marty McFly!"
}
```

And also in Quarkus log you should see a log entry:

```
Hello, Marty McFly!
```