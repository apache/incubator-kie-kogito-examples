# Payroll Sample Service

## Description

This is a sample Payroll service that is exposing a set of REST endpoints to:

* assign a payment date for the given employee
* calculate a tax rate for the given employee
* calculate the number of vacation days for the given employee

The service is defined as a decision service, using three sets of DMN decisions for each of the functions described
above.

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
./target/payroll-runner -Dquarkus.http.port=8082 -Dquarkus.http.host=localhost
```

## OpenAPI (Swagger) documentation

[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8082/openapi?format=json) - automatically generated and
included in this service - to determine all available operations exposed by this service. For easy readability you can
visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io)
.

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in Quarkus development mode, we also leverage
the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that
exposes [Swagger UI](http://localhost:8082/swagger-ui/) that you can use to look at available REST endpoints and send
test requests.

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /payments/date

Assigns payment date for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/payments/date
```

As response the employee details including the new payment date are returned.

### POST /taxes/rate

Calculates the tax rate for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/taxes/rate
```

As response the employee details including the new tax rate are returned.

### POST /vacations/days

Calculates the number of vacation days for the given employee:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/vacations/days
```

As response the employee details including the number of vacation days are returned.

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
minikube service --url payroll-service
```

You should see the URL in the terminal. Use it to make the calls to the `payroll-service`
