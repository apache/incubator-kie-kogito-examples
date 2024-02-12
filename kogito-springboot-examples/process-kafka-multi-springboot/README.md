# Process with Kafka

## Description

A quickstart project that deals with traveller processing carried by rules. It illustrates
how easy it is to make the Kogito processes and rules to work with Apache Kafka sending messages to different topics. 

This example shows

* consuming events from a Kafka topic and for each event start new process instance
* each process instance is expecting a traveller information in JSON format
* traveller is then processed by rules and based on the outcome of the processing (processed or not) traveller is
	* if successfully processed traveller information is logged and then updated information is send to another Kafka topic
	* if not processed traveller info is logged and then process instance finishes  sending reply to a different Kafka topic

<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>

* Diagram Properties (top)
<p align="center"><img src="docs/images/diagramProperties.png"></p>

* Diagram Properties (bottom)
<p align="center"><img src="docs/images/diagramProperties2.png"></p>

* Diagram Properties (process variables)
<p align="center"><img src="docs/images/diagramProperties3.png"></p>

* Start Message
<p align="center"><img src="docs/images/startMessage.png"></p>

* Start Message (Assignments)
<p align="center"><img src="docs/images/startMessageAssignments.png"></p>

* Process Traveler Business Rule (top)
<p align="center"><img src="docs/images/processTravelerBusinessRule.png"></p>

* Process Traveler Business Rule (bottom)
<p align="center"><img src="docs/images/processTravelerBusinessRule2.png"></p>

* Process Traveler Business Rule (Assignments)
<p align="center"><img src="docs/images/processTravelerBusinessRuleAssignments.png"></p>

* Process Traveler Gateway
<p align="center"><img src="docs/images/processedTravelerGateway.png"></p>

* Process Traveler Gateway Yes Connector
<p align="center"><img src="docs/images/processedTravelerYesConnector.png"></p>

* Process Traveler Gateway No Connector
<p align="center"><img src="docs/images/processedTravelerNoConnector.png"></p>

* Log Traveler Script Task
<p align="center"><img src="docs/images/logTravelerScriptTask.png"></p>

* Skip Traveler Script Task
<p align="center"><img src="docs/images/skipTravelerScriptTask.png"></p>

* Processed Traveler End Message
<p align="center"><img src="docs/images/processedTravelerEndMessage.png"></p>

* Processed Traveler End Message (Assignments)
<p align="center"><img src="docs/images/processedTravelerEndMessageAssignments.png"></p>

* Skip Traveler End
<p align="center"><img src="docs/images/skipTraveler.png"></p>


## Infrastructure requirements

This quickstart requires an Apache Kafka to be available and by default expects it to be on default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

### Compile and Run in Local Dev Mode

```sh
mvn clean compile spring-boot:run
```


### Package and Run using uberjar

```sh
mvn clean package
```

To run the generated native executable, generated in `target/`, execute

```sh
java -jar target/process-kafka-multi-springboot.jar
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

### Use the application

To make use of this application it is as simple as putting a message on `travellers` topic with following content

* To examine ProcessedTravellers topic and verify upcoming messages will be processed

Execute in a separate terminal session

```sh
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic processedtravellers
```

Execute in a separate terminal session

```sh
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic cancelledtravellers
```



* Send message that should be processed to Topic

```sh
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic travellers
```

Content (cloud event format)

```json
{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "travellers",
  "time": "2022-02-24T13:25:16+0000",
  "data": {
	"firstName" : "Jan",
	"lastName" : "Kowalski",
	"email" : "jan.kowalski@example.com",
	"nationality" : "Polish"
	}
}
```
One liner

```json
{"specversion": "0.3","id": "21627e26-31eb-43e7-8343-92a696fd96b1","source": "","type": "travellers", "time": "2022-02-24T13:25:16+0000","data": { "firstName" : "Jan", "lastName" : "Kowalski", "email" : "jan.kowalski@example.com", "nationality" : "Polish"}}
```

this will then trigger the successful processing of the traveller and put another message on `processedtravellers` topic.

To take the other path of the process put following message on `travellers` topic

* Send Message to Topic

```sh
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic travellers
```
With the following content (Cloud Event Format)

```json
{
  "specversion": "0.3",
  "id": "31627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "travellers",
  "time": "2022-02-24T13:25:16+0000",
  "data": {
	"firstName" : "John",
	"lastName" : "Doe",
    "email" : "john.doe@example.com",
    "nationality" : "American"
	}
}
```

One Liner

```json
{"specversion": "0.3","id": "31627e26-31eb-43e7-8343-92a696fd96b1","source": "","type": "travellers", "time": "2022-02-24T13:25:16+0000","data": { "firstName" : "John", "lastName" : "Doe", "email" : "john.doe@example.com", "nationality" : "American"}}
```

this will  result in message being send to `cancelledtravelers` topic.
