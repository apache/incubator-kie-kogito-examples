# Kogito Serverless Workflow - Python Hello World Example

## Description

This example contains a simple workflow definition that executes a python standard function library

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed
  - Python3 installed
  

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - LD_LIBRARY_PATH should include GRAALVM_HOME/lib/server
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
Also LD_LIBRARY_PATH should include GRAALVM_HOME/lib/server

```sh
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```sh
./target/serverless-workflow-python-quarkus-{version}-runner
```


### Submit a request

The service based on the JSON workflow definition can be access by sending an empty request to http://localhost:8080/python_helloworld

`curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{}' http://localhost:8080/python_helloworld`

It will return as result the current year
