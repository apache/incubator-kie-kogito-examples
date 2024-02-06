# Kogito Serverless Workflow - Job Application Events Example

## Description

This example showcases the use of [Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/main/workflow/spec) 
markup to create a job application decision workflow.
It also showcases the power of Kogito to create a completely event-driven services example.
The UI and workflow service communicate only over events (CloudEvents format) that are streamed
to Kafka topics.

Here is the overall architecture of this example:

<p align="center">
<img src="img/example-architecture.png" alt="Example Architecture"/>
</p>

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed
  - Apache Kafka installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Infrastructure requirements

This quickstart requires Apache Kafka to be available and by default expects it to be on default port and localhost.

* Install and Startup Kafka Server / Zookeeper

https://kafka.apache.org/quickstart

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:

```sh
docker-compose up
```  

In this way a container for Kafka will be started on port 9092.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

### Package and Run in JVM mode

```sh
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

or on Windows

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
./target/serverless-workflow-events-quarkus-runner
```

### Running the Example

After starting the example application you can access the front-end page at:

```text
http://localhost:8080/
```

You should see the following page:

<p align="center">
<img src="img/sw-example1.png" alt="Example1"/>
</p>

Fill in the "Submit New Applicant" form and submit it. This will send a cloud event
to Kafka which starts a new workflow instance. The workflow includes a rule function call
which evaluates the salary entered. Before the workflow execution ends
it sends a cloud event to Kafka. The UI subscribes to these events using SSE and 
updates the "Application Decision" table with the results, for example:

<p align="center">
<img src="img/sw-example2.png" alt="Example2"/>
</p>
