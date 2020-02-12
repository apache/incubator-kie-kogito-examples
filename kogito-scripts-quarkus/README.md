# Kogito script invocation

## Description

A quickstart project is the simplest hello world kind of example, it accepts input and replies with hello message.

This example shows

* invoking scripts from within process
		
<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>

* Diagram Properties
<p align="center"><img src="docs/images/diagramProperties.png"></p>

* Diagram Properties
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
./target/kogito-scripts-quarkus-{version}-runner
```

### Use the application

Examine OpenAPI via swagger UI at [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)


### Submit a request

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/scripts`  with following content 

```
{
"name" "john"
}

```

Complete curl command can be found below:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name" : "john"}' http://localhost:8080/scripts
```

After executing the above you should see a log similar to

<p align="center"><img src="docs/images/quarkusLog.png"></p>

And a message after the curl command such as

<p align="center"><img src="docs/images/consoleLog.png"></p>
