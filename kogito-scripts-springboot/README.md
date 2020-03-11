# Kogito script invocation

## Description

A quickstart project is the simplest hello world kind of example, it accepts input and replies with hello message.

This example shows

* invoking scripts from within process
			
<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>

* Diagram Properties (top)
<p align="center"><img src="docs/images/diagramProperties.png"></p>

* Diagram Properties (bottom)
<p align="center"><img src="docs/images/diagramProperties2.png"></p>

* Hello Script Task
<p align="center"><img src="docs/images/sayHelloScriptTask.png"></p>	

* Update Message Script Task
<p align="center"><img src="docs/images/updateMessageScriptTask.png"></p>


## Build and run

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

### Compile and Run in Local Dev Mode

```
mvn clean package spring-boot:run    
```


### Compile and Run using uberjar

```
mvn clean package 
```
  
To run the generated native executable, generated in `target/`, execute

```
java -jar target/kogito-scripts-sprintboot-{version}.jar
```

### Use the application


### Submit a request

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/scripts`  with following content 

```
{
"name" : "john"
}

```

Complete curl command can be found below:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name" : "john"}' http://localhost:8080/scripts
```

Log after curl executed
```
{"id":"ab5239e2-f497-4684-b337-5a44440b38dd","name":"john","message":"Hello john"}
```

Likewise on Springboot you should see a similar log to

```
Hello john"
```