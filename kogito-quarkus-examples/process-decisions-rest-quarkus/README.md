# Process with Decisions Integration through REST

## Description

This is an example project that shows the usage of decisions within processes. Decisions can be expressed in different domains or assets, such as DMN and DRL. 
The focus here is to show how to integrate decisions in a remote way using REST APIs where they can be deployed decoupled from the process service, for instance, in different microservices with its build and deployment pipelines. For convenience, in this example,  the decision assets are under the same project as the process that generates different endpoints for the process and decisions but under the same application, but in a real use case they could be placed in a different applications.

This example covers the following items:

* DMN to define a decision service
* DRL to define rules decision service
* How to integrate the process with decisions using REST
    * Service Task to call an external decision service
    * REST Work Item to call an external decision service

### The Traffic Process example:

It is based on the traffic violation evaluation process, where it is required to fetch Driver information, and based on this, it is first performed the license validation to check if the driver has a valid license (using a RuleUnit in a DRL) after the license validation it is then executed the violation evaluation defined as a DMN decision and following, it is checked in the process if the output contains information whether the driver was suspended or not, completing the process.

> There are two examples to show different approaches to do integration with decisions, they are pretty similar from the process definition perspective, the difference is related to the task that performs the call to the decision evaluation, either using a Service Task or a REST Work item.

#### Process using Service Tasks
  
![Traffic Process](docs/images/traffic-rules-dmn-service-task.png)

In this approach it requires coding, to provide an implementation in the application responsible to execute the task, in this case, a REST/HTTP call. The implementation is up to the user, but here it's been used what the platform provides, like the [Quarkus REST Client](https://quarkus.io/guides/rest-client). Details can be seen in the classes [LicenseValidationRestClient](src/main/java/org/kie/kogito/traffic/LicenseValidationRestClient.java) and [TrafficViolationRestClient](src/main/java/org/kie/kogito/traffic/TrafficViolationRestClient.java).

The BPMN file where this process is declared is [traffic-rules-dmn-service-task.bpmn](src/main/resources/traffic-rules-dmn-service-task.bpmn)

#### Process using REST Work Item
  
![Traffic Process](docs/images/traffic-rules-dmn-wih.png)

This is a declarative approach, it does not require to have any extra implementation, the REST/HTTP call is executed out-of-the-box by the engine. The information needed to execute the REST call, like the URL and HTTP method should be set in the Data Assignments in the REST Work Item.

The BPMN file where this process is declared is [traffic-rules-dmn-wih.bpmn](src/main/resources/traffic-rules-dmn-wih.bpmn)

---

* #### Process Properties
<img src="docs/images/process-properties.png" width=300/>

These are the properties defined for the process, the most important one in this section to pay attention is the ID because it is used in the REST endpoint generation referring to the path to interact with this process.

* #### Proces Variables

The variables used in the process itself, but the focus in this example are the classes that are used to define the POJOs to interact the process with decisions, that are the [Violation](src/main/java/org/kie/kogito/traffic/Violation.java), [Driver](src/main/java/org/kie/kogito/traffic/Driver.java), [Fine](src/main/java/org/kie/kogito/traffic/Fine.java) and [TrafficViolationResponse](src/main/java/org/kie/kogito/traffic/TrafficViolationResponse.java).

<img src="docs/images/process-variables.png" width=300/>

**Mapping data from Process to/from DMN**

It is important to mention DMN for instance can define the Data Type in its structure, but we can align all attributes names in a Java class that is used as process variables, in case the attribute names contain spaces or are not following java conventions we can use [Jackson](https://github.com/FasterXML/jackson) annotations to make the process variable POJOs aligned with DMN data types, for instance in the [Violation](src/main/java/org/kie/kogito/traffic/Violation.java) class, where it is mapped the `speedLimit` attribute as `Speed Limit` using `@JsonProperty` annotation, in this case, this attribute from the process variable with Violation can be seamlessly integrated Violation Data Type defined in DMN.

**Violation Data Type in DMN**

<img src="docs/images/violation-dmn-data-types.png" width=600/>


* #### Get Driver Task

Fetch for driver information, in this implementation it is just mocking a result, that simply fill with an expired license date in case the `driverId` is an odd number and with a valid date in case of an even number. In a real use case, it could be performing an external call to a service or a database to get this information.

The service task implementation is done in the [DriverService](src/main/java/org/kie/kogito/traffic/DriverService.java) class.

In the data assignment the input is the `driverId` and output is the `driver` variable, filled with all driver information.

* #### License Validation Task (DRL)

Represents the task to do the call to the DRL service.

**Service Task**

<img src="docs/images/license-validation-drl-service-task.png" width=150/>

The implementation properties where it is necessary to set the Java class implementing the task that executes the call should be set alongside the method. The URL configuration is done in the [application.properties](src/main/resources/application.properties).

<img src="docs/images/license-validation-drl-service-task-properties.png" width=300/>

The input and output mapping for this task is just the driver variable that is filled with license validation information.

![License Validation Service Data](docs/images/license-drl-service-task-data-mapping.png)


**REST Work Item**

<img src="docs/images/license-validation-drl-wih.png" width=150/>

The input and output mapping for this task is just the driver variable that is filled with license validation information. For REST Work Item the URL and HTTP Method are set as input parameters in the process itselt, that is differnt from the Service Task approach.

![License Validation WIH Data](docs/images/license-validation-drl-wih-data-mapping.png)


* #### Traffic Violation Task (DMN)
Similar to the License Validation Task, but it represents the task to do the call to the DMN service.

**Service Task**

<img src="docs/images/traffic-violation-drl-service-task.png" width=150/>

The implementation properties where it is necessary to set the Java class implementing the task that executes the call should be set alongside the method.

<img src="docs/images/traffic-violation-drl-service-task-properties.png" width=300/>

The input for this task is the `Driver` and `Violation` variables, and the output is the `Suspended` and `Fine` that are wrapped into the [TrafficViolationResponse](src/main/java/org/kie/kogito/traffic/TrafficViolationResponse.java).

![Traffic Violation Service Data](docs/images/traffic-violation-drl-service-task-data.png)


**REST Work Item**

<img src="docs/images/traffic-violation-drl-wih.png" width=150/>

The input for this task is the `Driver` and `Violation` variables, and the output is the `Suspended` and `Fine` that are wrapped into the [TrafficViolationResponse](src/main/java/org/kie/kogito/traffic/TrafficViolationResponse.java). For REST Work Item the URL and HTTP Method are set as input parameters in the process itselt, that is different from the Service Task approaach.

![Traffic Violation WIH Data](docs/images/traffic-violation-drl-wih-data.png)


* #### Suspended Task
Just an example task where it could be performed any action based on the condition in which the driver is suspended. In the current implementation, it is just logging the information in the console.


* #### Not Suspended Task
Just an example task where it could be performed any action based on the condition in which the driver is **not** suspended. In the current implementation, it is just logging the information in the console.

## Decisions

### License Validation - Rule Unit 

This decision consistis in rules which are evaluated to check if a driver's license is expired or not according to the expiration date and thus populating the result in the information in the driver variable.

The DRL file where this Rule Unit is declared is [LicenseValidationService.drl](src/main/resources/LicenseValidationService.drl) and the the Java class that contains the Rule Unit Data is [LicenseValidationService](src/main/java/org/kie/kogito/traffic/LicenseValidationService.java).

### Traffic Violation - DMN

This decision consists in a DMN that basically checks if a driver is suspended or not according to the violation and current driver points in its license.

![Traffic Violation - DMN](docs/images/traffic-violation-dmn.png)

The DMN file where this decision is declared is [TrafficViolation.dmn](src/main/resources/TrafficViolation.dmn)


## Build and run

### Prerequisites

You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed

When using native image compilation, you will also need:
  - GraalVM 19.3+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image
Note that the following configuration property needs to be added to `application.properties` in order to enable automatic registration of `META-INF/services` entries required by the workflow engine:
```
quarkus.native.auto-service-loader-registration=true
```

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/process-decision-rest-quarkus-runner
```

## OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/q/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/q/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

## Example Usage

Once the service is up and running we can invoke the REST endpoints and examine the logic.
There should be two process running under the following URLs:
* Process using Service Tasks: `http://localhost:8080/traffic_service`
* Process using REST Work Item: `http://localhost:8080/traffic_wih`

### Submit a request

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/traffic_service` or  `http://localhost:8080/traffic_wih`  with appropriate contents. See the following cases:

#### Valid License and Suspended Driver

Given data:

```json
{
    "driverId": "12345",
    "violation":{
        "Type":"speed",
        "Speed Limit": 100,
        "Actual Speed":140
    }
}
```

Submit the JSON object from above:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"driverId": "12345","violation":{"Type":"speed","Speed Limit": 100,"Actual Speed":140}}' http://localhost:8080/traffic_service
```

or

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"driverId": "12345","violation":{"Type":"speed","Speed Limit": 100,"Actual Speed":140}}' http://localhost:8080/traffic_wih
```

After the Curl command you should see a similar console log

```json
{
    "id": "e499326d-3bd2-4ddb-93b7-0f68f74a9673",
    "driverId": "12345",
    "driver": {
        "licenseExpiration": "2021-08-12T19:35:48.971+00:00",
        "validLicense": true,
        "Name": "Arthur",
        "State": "SP",
        "City": "Campinas",
        "Points": 13,
        "Age": 30
    },
    "trafficViolationResponse": {
        "Fine": {
            "Amount": 1000.0,
            "Points": 7
        },
        "Suspended": "Yes"
    },
    "violation": {
        "Code": null,
        "Date": null,
        "Type": "speed",
        "Speed Limit": 100,
        "Actual Speed": 140
    }
}
```

You can play with different attributes, if the `driverId` is an odd number in this case `12345` the license will be evaluated as valid because the DriverService generates a valid expiration date and DMN is evaluated, you can change the actual speed to 140 or 100 to simulate the driver suspension on the DMN evaluation.

If the `driverId` is an even number like `1234` the license will be evaluated as expired and the DMN will not be evaluated, so Suspended response attribute should be `null`.

#### Expired Valid License

Given data:

```json
{
    "driverId": "1234",
    "violation":{
        "Type":"speed",
        "Speed Limit": 100,
        "Actual Speed":110
    }
}
```

Submit the JSON object from above:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"driverId": "1234","violation":{"Type":"speed","Speed Limit": 100,"Actual Speed":110}}' http://localhost:8080/traffic_service
```

or

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"driverId": "1234","violation":{"Type":"speed","Speed Limit": 100,"Actual Speed":110}}' http://localhost:8080/traffic_wih
```

After the Curl command, you should see a similar console log

```json
{
    "id": "fffd3f08-045b-4935-a312-0901c4120f19",
    "driverId": "1234",
    "driver": {
        "licenseExpiration": "2021-08-11T19:43:44.130+00:00",
        "validLicense": false,
        "Name": "Arthur",
        "State": "SP",
        "City": "Campinas",
        "Points": 13,
        "Age": 30
    },
    "trafficViolationResponse": null,
    "violation": {
        "Code": null,
        "Date": null,
        "Type": "speed",
        "Speed Limit": 100,
        "Actual Speed": 110
    }
}
```
In this case the driver license is expired when the DRL is evaluated because the  DriverService generated an expired date for the driver's license thus DMN is not evaluated, so the `validLicense` is `false` and  `trafficViolationResponse` is `null`. 
