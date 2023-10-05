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

You can take a look at the [OpenAPI definition](http://localhost:8081/openapi?format=json) - automatically generated and
included in this service - to determine all available operations exposed by this service. For easy readability you can
visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io)
.

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in Quarkus development mode, we also leverage
the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that
exposes [Swagger UI](http://localhost:8081/swagger-ui/) that you can use to look at available REST endpoints and send
test requests.

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

## Deployment to Kubernetes, OpenShift or Minikube

This project is configured to run with Quarkus Kubernetes extensions. You can simply run:

```shell
# make sure that the docker env from minikube is enabled locally
$ eval $(minikube -p minikube docker-env)

# build the service, the image and deploy it on Minikube:
$ mvn clean install -Pminikube

# For Kubernetes or Knative just change the given profile to the respective platform (lower case).
```

> NOTE: If you're targeting a Kubernetes or OpenShift cluster, consider the resources created on `target/kubernetes`
> directory.

Please see the official [Quarkus Guide](https://quarkus.io/guides/deploying-to-kubernetes) for more information.

## Deploying to Knative

To be able to deploy to Knative you can follow
the [same guide mentioned above](https://quarkus.io/guides/deploying-to-kubernetes#knative). The needed information is
already added to the `application.properties` file.

## Testing on Minikube

Once you have deployed the three services, you can run the following command to expose the `onboarding-service` via Node
Port (the default option is already configured for you):

```shell
minikube service --url hr-service
```

You should see the URL in the terminal. Use it to make the calls to the `hr-service`
