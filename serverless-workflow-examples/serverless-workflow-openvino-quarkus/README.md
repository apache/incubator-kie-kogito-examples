# Kogito Serverless Workflow - Openvino Hello World Example

## Description

This example contains a workflow definition that emulates functionality exposed by [openvino hello world example](https://github.com/openvinotoolkit/openvino_notebooks/blob/main/notebooks/001-hello-world/001-hello-world.ipynb)

The flow, given a image file name containing a dog image, returns that dog race. 

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

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/openvino_helloworld
with following content 

```json
{
  "fileName": "path to a file containing a picture of a dog"
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"fileName": <path>}' http://localhost:8080/openvino_helloworld
```


The flow should return a message with the dog race as property `group`. 
