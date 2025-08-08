# Kogito Serverless Workflow - Funqy

## Description

This example showcases the use of [Serverless Workflow specification](https://github.com/cncf/wg-serverless/tree/main/workflow/spec) 
markup to call Funqy services via OpenAPi.

This example is composed of two modules, namely `sw-funqy-services` and `sw-funqy-workflow`.
`sw-funqy-services` contains the 3 Funqy functions and runs on port 8082.
`sw-funqy-workflow` contains just the workflow, the OpenApi definition and a simple UI and runs on port 8081.

This showcases that just with a workflow definition and an OpenApi definition you can 
orchestrate any services you want.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.9+ installed

When using native image compilation, you will also need: 
  - [GraalVm](https://www.graalvm.org/downloads/) 19.3.1+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

### Running the Example

First you need to start the `sw-funqy-services` module:

```sh
cd sw-funqy-services
mvn clean install quarkus:dev
```

this service will start on port 8082

Then we need to start our workflow service, namely `sw-funqy-workflow`

```sh
cd sw-funqy-workflow
mvn clean install quarkus:dev
```

Now access the demo UI on:

```text
http://localhost:8081
```

Type in a country name into the form (note currently only "Germany", "USA", "Brazil" and "Serbia" are supported).
This will trigger our workflow instances running on port 8081. 
The workflow execution will orchestrate our 3 Funqy functions (running on port 8082) in order
to gather all the country information, and will display it on the page.

Have fun :)