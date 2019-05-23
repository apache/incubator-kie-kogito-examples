# Onboarding Sample Service

## Description

This is a sample service that is exposing single REST endpoint to allow

* onboard new employees


## Installation

- Compile and Run

    ```
     mvn clean package quarkus:dev      
    ```

- Native Image (requires JAVA_HOME to point to a valid GraalVM)

    ```
    mvn clean package -Pnative
    ./target/onboarding-1.0.0-SNAPSHOT-runner -Dquarkus.http.port=8080 -Dquarkus.http.host=localhost
    ```
  
## Usage

There are one REST endpoint exposed

### post /onboarding

Allows to start onboarding procedure for given employee

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8080/onboarding                                                                                                
```


## Deployment to OpenShift

NOTE: Make sure that submarine S2I image builders are available to your OpenShift environment

### Build from local workspace

* Go to payroll project root directory
* Create new binary build and start it by uploading content of the current directory

```sh
oc new-build myproject/kaas-quarkus-centos-s2i --binary=true --name=onboarding-service-builder
oc start-build onboarding-service-builder --from-dir . --incremental=true
```

Once the build is completed create new build for runtime image

```sh
oc new-build --name onboarding-service --source-image=onboarding-service-builder --source-image-path=/home/submarine/bin:. --image-stream=kaas-quarkus-centos
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

You can inspect [swagger docs](http://localhost:8080/docs/swagger.json) to learn more about the service.


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
