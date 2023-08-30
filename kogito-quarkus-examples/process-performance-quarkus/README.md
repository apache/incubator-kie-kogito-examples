# Process Quarkus Performance

## Description

A set of BPMN processes used to manually run throughput tests on Quarkus in order to check performance.
All BPMN files share the same structure. They consist of one script, which invokes ``Thread.sleep`` to stop process execution as much time as indicated on ``delay`` argument, and they all publish a message 
to channel  _done_  when ended. 
The difference between them is the way they are started: 
* test.bpmn2 is started through a regular REST invocation
* kafkaTest.bpmn2 is started through a message on channel  _test_
* kafkaTestOtherChannel.bpmn2 is started through a message on channel  _test2_
 
This project is configured to run on Kafka (you can change broker and topic on application.properties). Therefore, you need to have
Kafka cluster installed and available over the network. Refer to [Kafka Apache site](https://kafka.apache.org/quickstart) for more information about how to install. 

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed
  - Apache Kafka 

When using native image compilation, you will also need:
  - [GraalVM 19.1.1](https://github.com/oracle/graal/releases/tag/vm-19.1.1) installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/process-quarkus-example-runner
```

## Example Usage

Once the service is up and running, to collect throughput numbers you need to manually change parameters in code of, compile and  run class ```org.acme.performance.client.MainRunner``` in process-performance-client project

