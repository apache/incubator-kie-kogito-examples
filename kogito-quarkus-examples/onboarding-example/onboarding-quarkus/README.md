# Onboarding Sample Service

## Description

This is the main onboarding service that exposes a single REST endpoint to onboard new employees.

The service is defined using a combination of 3 business processes to describe the steps required to onboard new
employees. This includes one main process that serves as the overall entry point and two sub-processes to handle the
interaction with HR and payroll.

## Installing and Running

### Compile and Run in Local Dev Mode

```
mvn clean package quarkus:dev    
```

### Compile and Run using Local Native Image
Note that the following configuration property needs to be added to `application.properties` in order to enable automatic registration of `META-INF/services` entries required by the workflow engine:
```
quarkus.native.auto-service-loader-registration=true
```

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute:

```
./target/onboarding-runner -Dquarkus.http.port=8080 -Dquarkus.http.host=localhost -Dorg.acme.kogito.onboarding.local=true
```

Please note the additional parameter to specify you are running the service locally. When running this service inside
kubernetes, it would take advantage of the service lookup feature to find other required service using labels (which
isn't available when running locally).


## OpenAPI (Swagger) documentation

[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and
included in this service - to determine all available operations exposed by this service. For easy readability you can
visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io)
.

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in Quarkus development mode, we also leverage
the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that
exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send
test requests.

## Usage

Once the services are up and running, you can use the following example to interact with the service.

### POST /onboarding

To start a new onboarding for the given employee, use following sample request:

```sh
curl -X POST -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8080/onboarding
```

This will onboard the given employee by the contacting the hr service (for validation, adding an employee identifier and
manager, etc.) and the payroll service (for adding information about payroll, taxation and vacation days). It will
return all employee information (including the newly generated information from the hr and payroll service) and the
status of the onboarding request. Note that subsequent calls with the same information (employee personal ID) will
result in a failed status as an employee can only be onboarded once.

Please note that the first execution of this request might take slightly longer, as some lazy loading and initialization
of various components might still be required. Subsequent calls should be a lot faster though.

## Deployment to Kubernetes, OpenShift or Minikube

This project is configured to run with Quarkus Kubernetes extensions. You can simply run:

```shell
# make sure that the docker env from minikube is enabled locally
$ eval $(minikube -p minikube docker-env)

# build the service, the image and deploy it on Minikube:
$ mvn clean install -Pminikube

# For Kubernetes or Knative just change the given profile to the respective platform (lower case).
```

Please see the official [Quarkus Guide](https://quarkus.io/guides/deploying-to-kubernetes) for more information.

## Deploying to Knative

To be able to deploy to Knative you can follow
the [same guide mentioned above](https://quarkus.io/guides/deploying-to-kubernetes#knative).

In addition, following permissions should be added to the default service account so the service discovery can properly
happen

```sh
oc policy add-role-to-group view system:serviceaccounts:onboarding-service -n istio-system
oc policy add-role-to-group knative-serving-core system:serviceaccounts:onboarding-service -n default
```

If using [Knative Serving Operator](https://github.com/knative/serving-operator) on OpenShift 4.x, the permissions that
need to be set are

```sh
oc adm policy add-role-to-user knative-serving-core system:serviceaccount:myproject:onboarding-service -n default
oc adm policy add-role-to-user view system:serviceaccount:myproject:onboarding-service -n istio-system
```

## Testing on Minikube

Once you have deployed the three services, you can run the following command to expose the `onboarding-service` via Node
Port (the default option is already configured for you):

```shell
minikube service --url onboarding-service
```

You should see the URL in the terminal. Use it to make the calls to the `onboarding-service`
