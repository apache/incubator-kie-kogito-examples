# Kogito Serverless Workflow Simple Greeting Example

## Description

This example contains two simple greeting workflow services. 
The services are described using both JSON and YAML formats as defined in the 
[CNCF Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/master/workflow/spec).

## Installing and Running

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

When using native image compilation, you will also need: 
  - [GraalVM 19.1.1](https://github.com/oracle/graal/releases/tag/vm-19.1.1) installed 
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Compile and Run in Local Dev Mode

```text
mvn clean package quarkus:dev    
```

### Compile and Run in JVM mode

```text
mvn clean package 
java -jar target/sw-quarkus-greeting-{version}-runner.jar    
```

or on windows

```text
mvn clean package
java -jar target\sw-quarkus-greeting-{version}-runner.jar
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```text
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```text
./target/sw-quarkus-greeting-{version}-runner
```

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/jsongreet'
with following content 

```json
{
  "workflowdata": {
   "name" : "john"
  }
}
```

Complete curl command can be found below:

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "john"}}' http://localhost:8080/jsongreet
```

Log after curl executed:

```text
{"id":"c20f04fe-46cya-44a2-9508-c8343a2f63df","workflowdata":{"name":"john"}}
```

In Quarkus you should see the log message printed:

```text
Hello john
```

Similarly the service based on the YAML workflow definition can be access by sending a request to http://localhost:8080/yamlgreet'
using the same content:

```json
{
  "workflowdata": {
   "name" : "john"
  }
}
``` 

Complete curl command can be found below:

```text
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"workflowdata" : {"name": "john"}}' http://localhost:8080/yamlgreet
```
 
In Quarkus you should again see the log message:

```text
Hello john
```