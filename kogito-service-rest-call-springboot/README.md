# Kogito Service invocation with REST call

## Description

A quickstart project that processes users in the system. It's main purpose is to to call external REST service
to load a given user by its username.

This example shows

* invoking remote REST service
* control flow based on service calls	
		
* Diagram	
<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>

* Diagram Properties
<p align="center"><img src="docs/images/diagramProperties.png"></p>

* Diagram Properties
<p align="center"><img src="docs/images/diagramProperties2.png"></p>

* Diagram Properties
<p align="center"><img src="docs/images/diagramProperties3.png"></p>

* Find User Service Call
<p align="center"><img src="docs/images/findUserServiceRestCall.png"></p>

* Find User Service Call
<p align="center"><img src="docs/images/findUserServiceRestCall2.png"></p>

* Find User Gateway Yes
<p align="center"><img src="docs/images/findUserGatewayYesConnector.png"></p>

* Find User Gateway No
<p align="center"><img src="docs/images/findUserGatewayNoConnector.png"></p>

* Audit User Service Rest Call
<p align="center"><img src="docs/images/auditUserServiceRestCall.png"></p>

* Audit User Service Rest Call
<p align="center"><img src="docs/images/auditUserServiceRestCall2.png"></p>

## Build and run

### Prerequisites
 
You will need:
  - Java 11+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

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

After the above command you should see some log on Springboot sush as following

* Springboot Log
<p align="center"><img src="docs/images/springbootLog.png"></p>

To test the other route possible for unknown user send request to `http://localhost:8080/users`  with following content 

```
{
"username" : "nonexisting"
}

```


Complete curl command can be found below:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"username" : "nonexisting"}' http://localhost:8080/users
```

After the above command nothing will show on Springboot log as the user is skipped but you should see the following on terminal after curl

* Curl Log
<p align="center"><img src="docs/images/curlLogNonExisting.png"></p>
