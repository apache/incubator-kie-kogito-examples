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
./target/hr-runner -Dquarkus.http.port=8081 -Dquarkus.http.host=localhost
```
  
## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8081/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in Quarkus development mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8081/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /id

Assigns employee id and email address for given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '
{
  "employee": {
    "firstName": "Mark",
    "lastName": "Test",
    "personalId": "xxx-yy-zzz",
    "birthDate": "2012-12-10T14:50:12.123+02:00",
    "address": {
      "country": "US",
      "city": "Boston",
      "street": "any street 3",
      "zipCode": "10001"
    }
  }
}
' http://localhost:8081/id
```

As response the employee details including the new employee id and email address are returned.

### POST /employeeValidation

Allows to verify if the given employee requires registration:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '
{
  "employee": {
    "firstName": "Mark",
    "lastName": "Test",
    "personalId": "xxx-yy-zzz",
    "birthDate": "2012-12-10T14:50:12.123+02:00",
    "address": {
      "country": "US",
      "city": "Boston",
      "street": "any street 3",
      "zipCode": "10001"
    }
  }
}
' http://localhost:8081/employee-validation/first
```

### POST /department

Assigns department and manager for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '
{
  "employee": {
    "firstName": "Mark",
    "lastName": "Test",
    "personalId": "xxx-yy-zzz",
    "birthDate": "2012-12-10T14:50:12.123+02:00",
    "address": {
      "country": "US",
      "city": "Boston",
      "street": "any street 3",
      "zipCode": "10001"
    }
  }
}
' http://localhost:8081/department/first
```

## Deployment to OpenShift

NOTE: Make sure that kogito S2I image builders are available to your OpenShift environment

### Build from local workspace

* Go to hr project root directory
* Create new binary build and start it by uploading content of the current directory

```sh
oc new-build myproject/kogito-builder --env RUNTIME_TYPE=quarkus --binary=true --name=hr-service-builder
oc start-build hr-service-builder --from-dir . --incremental=true 
```

Once the build is completed create new build for runtime image

```sh
oc new-build --name hr-service --source-image=hr-service-builder --env RUNTIME_TYPE=quarkus --env NATIVE=true --source-image-path=/home/kogito/bin:. --image-stream=kogito-runtime-native
```

Next create application for the runtime image

```sh
oc new-app hr-service:latest -l department/first=process,id=process,employee-validation/first=process
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
