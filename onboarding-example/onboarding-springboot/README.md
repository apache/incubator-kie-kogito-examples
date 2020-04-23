# Onboarding Sample Service

## Description

This is the main onboarding service that exposes a single REST endpoint to onboard new employees.

The service is defined using a combination of 3 business processes to describe the steps required to onboard new employees.  This includes one main process that serves as the overall entry point and two sub-processes to handle the interaction with HR and payroll. 

## Installing and Running

### Compile and Run in Local Dev Mode

```
mvn clean package spring-boot:run
```

### Running with persistence enabled

Kogito supports runtime persistence that is backed by Inifinispan. So to be able to enable this you need to have 
Infinispan server installed and available over the network. By default it expects it to be at (it can be configured via application.properties file located in src/main/resources)

```
localhost:11222
```

You can install Inifinispan server by downloading it from [https://infinispan.org/download/](official website) version to be used in 10.0.0.Beta4


Once Inifispan is up and running you can build this project with `-Ppersistence` to enable additional processing
during the build. Next you start it in exact same way as without persistence.

This extra profile in maven configuration adds additional dependencies needed to work with infinispan as persistent store. 

## Usage

Once the services are up and running, you can use the following example to interact with the service.

### POST /onboarding

To start a new onboarding for the given employee, use following sample request:

```sh
curl -X POST -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8080/onboarding
```

This will onboard the given employee by the contacting the hr service (for validation, adding an employee identifier and manager, etc.) and the payroll service (for adding information about payroll, taxation and vacation days).  It will return all employee information (including the newly generated information from the hr and payroll service) and the status of the onboarding request.  Note that subsequent calls with the same information (employee personal ID) will result in a failed status as an employee can only be onboarded once.

Please note that the first execution of this request might take slightly longer, as some lazy loading and initialization of various components might still be required.  Subsequent calls should be a lot faster though.

## Deployment to OpenShift

NOTE: Make sure that kogito S2I image builders are available to your OpenShift environment

### Build from local workspace

* Go to payroll project root directory
* Create new binary build and start it by uploading content of the current directory

```sh
oc new-build myproject/kogito-springboot-ubi8-s2i --binary=true --name=onboarding-service-builder
oc start-build onboarding-service-builder --from-dir . --incremental=true
```

Once the build is completed create new build for runtime image

```sh
oc new-build --name onboarding-service --source-image=onboarding-service-builder --source-image-path=/home/kogito/bin:. --image-stream=kogito-springboot-ubi8
```

Next create application for the runtime image

```sh
oc new-app onboarding-service:latest -l onboarding=process -e NAMESPACE=myproject
```

and lastly create the route for it

```sh
oc expose svc/onboarding-service
```

Since onboarding service uses discovery of services that it's going to interact with, you need to give view access to 
default service account

``` sh
# adding role 'view' to the system account (-z) named 'default'
# see https://docs.openshift.com/container-platform/latest/architecture/additional_concepts/authorization.html#roles
oc policy add-role-to-user view -z default
```

You can now use the onboarding service on OpenShift!

## Knative

To be able to deploy to Knative there is a template script that can be used to directly deploy 
image from docker hub, just execute following command

```sh
oc apply -f knative/knative-onboarding-service.yaml
```

In addition following permissions should be added to the default service account so the
service discovery can properly happen

```sh
oc policy add-role-to-group view system:serviceaccounts:default -n istio-system
oc policy add-role-to-group knative-serving-core system:serviceaccounts:default -n default
```

If using [Knative Serving Operator](https://github.com/knative/serving-operator) on OpenShift 4.x, the permissions that need to be set are

```sh
oc adm policy add-role-to-user knative-serving-core system:serviceaccount:myproject:default -n default
oc adm policy add-role-to-user view system:serviceaccount:myproject:default -n istio-system    
```