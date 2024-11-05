# Process Business Calendar Example

This project is an illustrative example demonstrating the impact of a business calendar on process execution within a Quarkus application . It showcases a business process involving credit card bill processing, which adapts to a business calendar defined in `calendar.properties`. This configuration modifies timer behaviors to respect working hours, holidays, and other schedule-based constraints.

### Main Components

**BPMN2-BusinessCalendarBankTransaction.bpmn2**:
Defines the workflow for processing credit card transactions. 
Includes tasks such as processing the bill, verifying payment, handling boundary timers, and potential cancellation.

**CreditCardService.java**:
Implements the logic for handling credit card payment processes.

**calendar.properties**:
Configures business hours, holidays, and other calendar parameters that affect job scheduling and timer behavior.

### BPMN Process Details

The BPMN model (`BPMN2-BusinessCalendarBankTransaction.bpmn2`) defines a workflow that includes the following main elements:
<p align="center"><img width=50% height=50% src="docs/images/CreditCardModel.png"></p>

### Start Event

The initial trigger that starts the credit card bill processing workflow.

### Process Credit Bill
* Process Credit Bill Properties (Top)
  <p align="center"><img width=75% height=50% src="docs/images/ProcessCreditBillTop.png"></p>

* Process Credit Bill Properties (Bottom)
  <p align="center"><img width=75% height=50% src="docs/images/ProcessCreditBillBottom.png"></p>
* Process Credit Card Bill Assignments
  <p align="center"><img width=75% height=50% src="docs/images/ProcessCreditBillAssign.png"></p>

### Verify Payment
A service task where the credit card details are validated, ensuring the payment is processed under valid terms.

* Verify Payment
  <p align="center"><img width=75% height=50% src="docs/images/VerifyPayment.png"></p>

### Timer

Attached to a human task to simulate waiting for manual confirmation or user action. This timer can be configured to react differently based on the presence of the business calendar.
<p align="center"><img width=75% height=50% src="docs/images/Timer.png"></p>

### Cancel Payment
Executed if the timer expires without action, leading to the cancellation of the payment process and notifying that the transaction failed.

* Cancel Payment (Top)
  <p align="center"><img width=75% height=50% src="docs/images/CancelPaymentTop.png"></p>

* Cancel Payment (Bottom)
  <p align="center"><img width=75% height=50% src="docs/images/CancelPaymentBottom.png"></p>

* Cancel Payment Assignments
  <p align="center"><img width=75% height=50% src="docs/images/CancelPaymentAssign.png"></p>

### Settle Payment

The final step where the payment is settled successfully, completing the process.

* Settle Payment (Top)
 <p align="center"><img width=75% height=50% src="docs/images/SettlePaymentTop.png"></p>

* Settle Payment (Bottom)
<p align="center"><img width=75% height=50% src="docs/images/SettlePaymentBottom.png"></p>

* Settle Payment Assignments
<p align="center"><img width=75% height=50% src="docs/images/SettlePaymentAssign.png"></p>

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

```sh
./target/process-usertasks-quarkus-runner
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

##curl command can be found below:

### To start workflow

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{
    "id": "test-instance",
    "creditCardNumber": null,
    "creditCardDetails": {
        "cardNumber": "434354343",
        "status": "Bill Due"
    }
}' http://localhost:8080/BusinessCalendarCreditBill
```

### To check Active Tasks

```sh
curl -X GET -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/BusinessCalendarCreditBill/<instance-id>/tasks
```

### To check user tasks

```sh
curl -X GET -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/usertasks/instance
```

## How `calendar.properties` affects the functionality

The `calendar.properties` file, when present, activates business calendar functionality that influences timer behavior in the BPMN model. It can delay job execution based on defined working hours, days of the week, and holidays.

### Example `calendar.properties`

```properties
business.start.hour=0
business.end.hour=24
business.hours.per.day=24
business.days.per.week=7
business.holiday.date.format=yyyy-MM-dd
```
## Without calendar.properties
* POST Request: The workflow is initiated successfully, and timers trigger immediately based on their configured duration.  
* GET Request for Active Tasks: The response includes active tasks with an activate phase, confirming immediate activation without delay.  
* Example Test Results Without calendar.properties:  


* POST
```json
{
  "id": "0a4105cc-54fb-4d17-a2bd-9b660e98df75",
  "creditCardNumber": null,
  "creditCardDetails": {
    "cardNumber": "434354343",
    "status": "Bill Due"  
    }
}
```

* GET
```json
[
    {
        "id": "9070919b-a836-4193-bb37-d331bdb11df4",
        "name": "Task",
        "state": 1,
        "phase": "activate",
        "phaseStatus": "Activated",
        "parameters": {},
        "results": {}
    }
]
```
## With calendar.properties
* POST Request: The workflow starts successfully, but timer activation respects the constraints defined in calendar.properties.
* GET Request for Active Tasks: The response may return 404 Not Found, indicating that the timer has not triggered due to scheduling delays imposed by the business calendar (e.g., holidays or non-working periods).  

* Example Test Results With calendar.properties:


* POST
```json
{
    "id": "b2b1d0bb-7946-4be6-81eb-0da35ff4a5d0",
    "creditCardNumber": null,
    "creditCardDetails": {
        "cardNumber": "434354343",
        "status": "Bill Due"
    }
}
```

* GET
```
404 Not Found
```

## Comparision with and without calendar.properties
* Without calendar.properties:  
Timers fire immediately after their configured interval, activating tasks without delay.

* With calendar.properties:  
The timer may delay job execution if the current date is defined as a holiday or falls outside of working hours, resulting in inactive tasks or 404 Not Found responses when checking for active tasks.

## Configuring holiday in calendar.properties

```properties
business.holiday.date.format=yy-MM-dd
business.holiday.date=2024-11-05,2024-12-25
```

### For repeated holidays in a year
```properties
business.holiday.date.format=dd/MM
business.holiday.date=14/11,25/12, 01/01
```

### Weekend days
```properties
business.weekend.days=7,1 
```
