# Kogito Serverless Workflow - Knative custom function

## Description

This example contains a simple workflow service that illustrates how to use Serverless Workflow custom function to invoke Knative services.

This example consists of a workflow and a remote service that both will be deployed to Knative.

The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6++ installed
    When using native image compilation, you will also need:
  - [GraalVm](https://www.graalvm.org/downloads/) 22.3.0+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.
  - minikube (https://minikube.sigs.k8s.io/docs/start/)
  - Knative [quickstarts](https://knative.dev/docs/getting-started/quickstart-install/)
  - Run `eval $(minikube -p minikube docker-env --profile knative)` to build the images in your internal Minikube registry.
  
### Deploy the `custom-function-knative-service` project to Knative

From the `custom-function-knative-service` directory run:

```sh
mvn clean package -Dquarkus.kubernetes.deploy=true -Dquarkus.container-image.group=dev.local/<your_user>
```

Check if the service was successfully deployed:

```sh
kn service list
```

You should see something similar to (URL should change):

```sh
NAME                              URL                                                                      LATEST                                  AGE   CONDITIONS   READY   REASON
custom-function-knative-service   http://custom-function-knative-service.default.10.109.169.193.sslip.io   custom-function-knative-service-00001   13s   3 OK / 3     True    
```

### Deploy the `workflow` project to Knative

From the `workflow` directory run:

```sh
mvn clean package -Dquarkus.kubernetes.deploy=true -Dquarkus.container-image.group=dev.local/<your_user>
```

Check if the service was successfully deployed:

```sh
kn service list
```

You should see both services deployed similar to  (URLs should change):

```sh
NAME                              URL                                                                      LATEST                                  AGE     CONDITIONS   READY   REASON
custom-function-knative-service   http://custom-function-knative-service.default.10.109.169.193.sslip.io   custom-function-knative-service-00001   3m53s   3 OK / 3     True    
workflow                          http://workflow.default.10.109.169.193.sslip.io                          workflow-00001                          12s     3 OK / 3     True        
```

### Submit a request with a plain JSON object 

The service based on the JSON workflow definition can be accessed by sending a request to `<URL>/plainJsonKnativeFunction` (replace `<URL>` with the URL returned by the above command) with the following content:

```json
{
  "name": "Kogito"
}
```

Complete curl command can be found below (replace `<URL>` with the URL for the `workflow` service returned by the `kn service list` command):

```sh
curl -X 'POST' \
  '<URL>/plainJsonKnativeFunction' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "name": "Kogito" }'
```

Should return something like this ("id" will change):

```json
{"id":"87cf8275-782d-4e0b-a9be-a95f95c9c190","workflowdata":{"name":"Kogito","greeting":"Greetings from Serverless Workflow, Kogito"}}
```

### Submit a request with a CloudEvent

The service based on the JSON workflow definition can be accessed by sending a request to `<URL>/cloudEventKnativeFunction` (replace `<URL>` with the URL returned by the above command) with the following content:

```json
{
  "cloudevent": {
    "specversion": "1.0",
    "source": "org.acme.source",
    "type": "test" 
  }
}
```

[NOTE]
====
Kogito Serverless Workflow generates a CloudEvent ID based on `source` and the workflow instance ID. In case an ID is set, Kogito Serverless Workflow will ignore it and use a generated one.
====

Complete curl command can be found below (replace `<URL>` with the URL for the `workflow` service returned by the `kn service list` command):

```sh
curl -X 'POST' \
  '<URL>/cloudEventKnativeFunction' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "cloudevent": { "specversion": "1.0", "source": "org.acme.source", "type": "test" }}'
```

Should return something like this ("id" will change):

```json
{"id":"8dc00353-c1c6-45e9-845d-e9188d103f50","workflowdata":{"id":"response-of-org.acme.source_8dc00353-c1c6-45e9-845d-e9188d103f50","specversion":"1.0","source":"cloudEventFunction","type":"annotated"}}
```