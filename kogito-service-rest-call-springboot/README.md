# Kogito Service invocation with REST call

## Description

A quickstart project that processes users in the system. It's main purpose is to to call external REST service
to load a given user by its username.

This example shows

* invoking remote REST service
* control flow based on service calls	
	
<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>


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
java -jar target/kogito-service-rest-call-sprintboot-{version}.jar
```

### Use the application


### Submit a user name

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/users`  with following content 

```
{
"username" : "test"
}

```

Complete curl command can be found below:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"username" : "test"}' http://localhost:8080/users
```
