# Payroll Sample Service

## Description

This is a sample Payroll service that is exposing a set of REST endpoints to:

* assign a payment date for the given employee
* calculate a tax rate for the given employee
* calculate the number of vacation days for the given employee

The service is defined as a decision service, using three sets of DMN decisions for each of the functions described above.

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
./target/payroll-{version}-runner -Dquarkus.http.port=8082 -Dquarkus.http.host=localhost
```
Please replace {version} with the actual version of kogito you are trying to use, e.g. 8.0.0-SNAPSHOT.
  
## Swagger documentation

You can take a look at the [swagger definition](http://localhost:8082/docs/swagger.json) - automatically generated and included in this service - to determine all available operations exposed by this service.  For easy readability you can visualize the swagger definition file using a swagger UI like for example available [here](https://editor.swagger.io). In addition, various clients to interact with this service can be easily generated using this swagger definition.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /paymentDate

Assigns payment date for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/paymentDate
```

As response the employee details including the new payment date are returned.

### POST /taxRate

Calculates the tax rate for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/taxRate
```

As response the employee details including the new tax rate are returned.

### POST /vacationDays

Calculates the number of vacation days for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/vacationDays
```

As response the employee details including the number of vacation days are returned.

## Deployment to OpenShift

NOTE: Make sure that kogito S2I image builders are available to your OpenShift environment

### Build from local workspace

* Go to payroll project root directory
* Create new binary build and start it by uploading content of the current directory

```sh
oc new-build myproject/kogito-quarkus-centos-s2i --binary=true --name=payroll-service-builder
oc start-build payroll-service-builder --from-dir . --incremental=true
```

Once the build is completed create new build for runtime image

```sh
oc new-build --name payroll-service --source-image=payroll-service-builder --source-image-path=/home/kogito/bin:. --image-stream=kogito-quarkus-centos
```

Next create application for the runtime image

```sh
oc new-app payroll-service:latest -l taxRate=process,vacationDays=process,paymentDate=process
```

and lastly create the route for it

```sh
oc expose svc/payroll-service
```

## Knative

To be able to deploy to knative there is a template script that can be used to directly deploy 
image from docker hub, just execute following command

```sh
oc apply -f knative/knative-payroll-service.yaml
```