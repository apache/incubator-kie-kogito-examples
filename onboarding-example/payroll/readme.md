# Payroll Sample Service

## Description

This is a sample service that is exposing set of REST endpoints to allow

* assign payment date for given employee
* calculate tax rate for given employee
* calculate vacation days for given employee


## Installation

- Compile and Run

    ```
     mvn clean package quarkus:dev   
    ```

- Native Image (requires JAVA_HOME to point to a valid GraalVM)

    ```
    mvn clean package -Pnative
    ./target/payroll-1.0.0-SNAPSHOT-runner -Dquarkus.http.port=8082 -Dquarkus.http.host=localhost
    ```
  
## Usage

There are three REST endponts exposed

### post /paymentDate

Assigns payment date for given employee

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/paymentDate                                                                                                 
```

### post /taxRate

Calculates tax rate for given employee

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/taxRate                                                                                                 
```

### post /vacationDays

Assigns department and manager for given employee

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8082/vacationDays                                                                                                
```


## Deployment to OpenShift

NOTE: Make sure that kogito S2I image builders are available to your OpenShift environment

### Build from local workspace

* Go to payroll project root directory
* Create new binary build and start it by uploading content of the current directory

```sh
oc new-build myproject/kaas-quarkus-centos-s2i --binary=true --name=payroll-service-builder
oc start-build payroll-service-builder --from-dir . --incremental=true
```

Once the build is completed create new build for runtime image

```sh
oc new-build --name payroll-service --source-image=payroll-service-builder --source-image-path=/home/kogito/bin:. --image-stream=kaas-quarkus-centos
```

Next create application for the runtime image

```sh
oc new-app payroll-service:latest -l taxRate=process,vacationDays=process,paymentDate=process
```

and lastly create the route for it

```sh
oc expose svc/payroll-service
```

You can inspect [swagger docs](http://localhost:8082/docs/swagger.json) to learn more about the service.

## Knative

To be able to deploy to knative there is a template script that can be used to directly deploy 
image from docker hub, just execute following command

```sh
oc apply -f knative/knative-payroll-service.yaml
```
