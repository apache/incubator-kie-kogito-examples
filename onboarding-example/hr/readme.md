# HR Sample Service

## Description

This is a sample HR service that is exposing set of REST endpoints to:

* verify if the given employee requires registration
* assign employee id and email address
* assign department and manager

The service is defined as a decision service, using three sets of DRL rules for each of the functions described above.

## Installing and Running

### Compile and Run in Local Dev Mode

```
mvn clean package quarkus:dev    
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute:

```
./target/hr-{version}-runner -Dquarkus.http.port=8081 -Dquarkus.http.host=localhost
```
Please replace {version} with the actual version of kogito you are trying to use, e.g. 8.0.0-SNAPSHOT.
  
## Swagger documentation

You can take a look at the [swagger definition](http://localhost:8081/docs/swagger.json) - automatically generated and included in this service - to determine all available operations exposed by this service.  For easy readability you can visualize the swagger definition file using a swagger UI like for example available [here](https://editor.swagger.io). In addition, various clients to interact with this service can be easily generated using this swagger definition.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /id

Assigns employee id and email address for given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "2012-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8081/id
```

As response the employee details including the new employee id and email address are returned.

### POST /employeeValidation

Allows to verify if the given employee requires registration:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "2012-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8081/employeeValidation
```

### POST /department

Assigns department and manager for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "2012-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8081/department
```

## Deployment to OpenShift

NOTE: Make sure that kogito S2I image builders are available to your OpenShift environment

### Build from local workspace

* Go to hr project root directory
* Create new binary build and start it by uploading content of the current directory

```sh
oc new-build myproject/kogito-quarkus-centos-s2i --binary=true --name=hr-service-builder
oc start-build hr-service-builder --from-dir . --incremental=true
```

Once the build is completed create new build for runtime image

```sh
oc new-build --name hr-service --source-image=hr-service-builder --source-image-path=/home/kogito/bin:. --image-stream=kogito-quarkus-centos
```

Next create application for the runtime image

```sh
oc new-app hr-service:latest -l department=process,id=process,employeeValidation=process
```

and lastly create the route for it

```sh
oc expose svc/hr-service
```

## Knative

To be able to deploy to knative there is a template script that can be used to directly deploy 
image from docker hub, just execute following command

```sh
oc apply -f knative/knative-hr-service.yaml
```