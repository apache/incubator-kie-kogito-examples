# Kogito Serverless Workflow - Python Hello World Example

## Description

This example contains a simple workflow definition that executes numpy generator

## Installing and Running

### Prerequisites
 
You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed
  - Python3 installed
  - Jep installed. Instructions [here](https://github.com/ninia/jep#installation) 
  - Python required libraries. Run `pip install -r requirements.txt`. Requirements.txt is on example root path. 


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

### Submit a request

The service based on the JSON workflow definition can be access by sending an empty request to http://localhost:8080/python_helloworld

`curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{}' http://localhost:8080/python_helloworld`

It will return as result an array of 3 numbers. 
