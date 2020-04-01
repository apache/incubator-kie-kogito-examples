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

In addition, it takes advantage of MicroProfile fault tolerance support to fallback if there are any errors
during REST service invocation.

## Build and run

### Prerequisites
 
You will need:
  - Java 11+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

When using native image compilation, you will also need: 
  - GraalVM 19.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```
mvn clean package quarkus:dev    
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.


### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```
./target/kogito-service-rest-call-quarkus-{version}-runner
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.


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

After the above command you should see some log on Quarkus such as following

* Quarkus Log
<p align="center"><img src="docs/images/quarkusLog.png"></p>

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

After the above command nothing will show on Quarkus log as the user is skipped but you should see the following on terminal after curl

* Curl Log
<p align="center"><img src="docs/images/curlLogNonExisting.png"></p>
