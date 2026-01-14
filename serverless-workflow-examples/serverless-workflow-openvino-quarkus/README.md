# Kogito Serverless Workflow - Openvino Hello World Example

## Description

This example contains a workflow definition that emulates functionality exposed by [openvino hello world example](https://github.com/openvinotoolkit/openvino_notebooks/blob/main/notebooks/001-hello-world/001-hello-world.ipynb)

The flow, given an image file name containing a dog image, returns that dog's race. 

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.11+ installed
  - Python 3+ installed. In Linux system is usually pre-installed. In case you have an older version or you are not using Linux, check [here](https://wiki.python.org/moin/BeginnersGuide/Download)
  - Pip installed. In case it not there, run `python -m ensurepip --upgrade`
  - Python required libraries, equivalent to run `pip install -r requirements.txt` and `pip install jep`, will be automatically installed when running Maven. 
  

> **_NOTE:_** Requirements.txt install Jep as the last one because it depends on the NumPy library to work correctly.

When using native image compilation, you will also need:

   - GraalVm 22.3.2+ installed
   - Environment variable GRAALVM_HOME set accordingly
   - LD_LIBRARY_PATH should include GRAALVM_HOME/lib/server

Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too. You also need 'native-image' installed in GraalVM (using `gu install native-image`). Please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```

### Compile and Run in JVM mode

```sh
mvn clean package 
java -jar target/quarkus-app/quarkus-run.jar
```

### Compile and Run using Local Native Image

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation
Also LD_LIBRARY_PATH should include GRAALVM_HOME/lib/server

```sh
mvn clean package -Pnative
```

It will take a while, once finished, run the generated executable.

```sh
./target/serverless-workflow-openvino-quarkus-{version}-runner
```


### Submit a request

Once the server is running, you can test it by sending a request to `http://localhost:8080/openvino_helloworld`
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
