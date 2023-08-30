# Process Springboot performance

## Description

# Process Quarkus Performance

## Description

A set of BPMN processes used to manually run throughput tests on Springboot  in order to check performance.
All BPMN files share the same structure. They consist of one script, which invokes ``Thread.sleep`` to stop process execution as much time as indicated on ``delay`` argument, and they all publish a message 
to channel  _done_  when ended. 
The difference between them is the way they are started: 
* test.bpmn2 is started through a regular REST invocation
* kafkaTest.bpmn2 is started through a message on channel  _test_
* kafkaTestOtherChannel.bpmn2 is started through a message on channel  _test2_
 
This project is configured to run on Kafka (you can change broker and topic on application.properties). Therefore, you need to have
Kafka cluster installed and available over the network. Refer to [Kafka Apache site](https://kafka.apache.org/quickstart) for more information about how to install. 

## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed
  - Apache Kafka

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
java -jar target/process-performance-springboot.jar
```


## Example Usage

Once the service is up and running, to collect throughput numbers you need to manually change parameters in code of, compile and  run class ```org.acme.performance.client.MainRunner``` in process-performace-client project

