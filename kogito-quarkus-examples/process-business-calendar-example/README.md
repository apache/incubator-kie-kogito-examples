# Process Business Calendar Example

This project is an illustrative example demonstrating the impact of a business calendar on process execution within a Quarkus application. It showcases a business process involving credit card bill processing, which adapts to a business calendar defined in calendar.properties. This configuration modifies timer behaviors to respect working hours, holidays, and other schedule-based constraints.

### Main Components

**BPMN2-BusinessCalendarBankTransaction.bpmn2**:
Defines the workflow for processing credit card transactions. 
Includes tasks such as processing the credit bill, verifying payment, handling timers, cancelling and bill settlement.

**CreditCardService.java**:
Implements the logic for handling credit card payment processes.

**calendar.properties**:
Configures business hours, holidays, and other calendar properties that affect scheduling and timer behavior.

### BPMN Process Details

The BPMN model (`BPMN2-BusinessCalendarBankTransaction.bpmn2`) defines a workflow that includes the following main elements:
<p align="center"><img width=75% height=50% src="docs/images/CreditCardModel.png"></p>

### Start Event

The initial trigger that starts the credit card bill processing workflow.

### Process Credit Bill
* Process Credit Bill Properties (Top)
  <p align="center"><img width=75% height=50% src="docs/images/ProcessCreditBillTop.png"></p>

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
Executed if the timer expires without human action, leading to the cancellation of the payment process.

* Cancel Payment (Top)
  <p align="center"><img width=75% height=50% src="docs/images/CancelPaymentTop.png"></p>

* Cancel Payment Assignments
  <p align="center"><img width=75% height=50% src="docs/images/CancelPaymentAssign.png"></p>

### Settle Payment

The final step where the payment is settled successfully on manual verification.

* Settle Payment (Top)
 <p align="center"><img width=75% height=50% src="docs/images/SettlePaymentTop.png"></p>

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

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/q/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

## curl command can be found below:

### To start the process

```sh
curl -X POST http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-d '{"creditCardNumber": null, "creditCardDetails": {"cardNumber": "434353433", "status": "Bill Due"}}'

```

### To retrieve instances

```sh
curl -X GET http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-H "Accept: application/json"

```
### To retrieve status of particular instance using id

```sh
curl -X GET http://localhost:8080/BusinessCalendarCreditBill/{id} \
-H "Content-Type: application/json" \
-H "Accept: application/json"

```

## Comparison of timer with and without calendar.properties

### Testing without calendar.properties
Without the calendar properties file, the behavior of the timer depends on the system current time settings (Default properties)

* business.days.per.week - specifies number of working days per week (default 5)
* business.hours.per.day - specifies number of working hours per day (default 8)
* business.start.hour - specifies starting hour of work day (default 9)
* business.end.hour - specifies ending hour of work day (default 17)
* business.holidays - specifies holidays in yyy-MM-dd format
* business.holiday.date.format - specifies holiday date format used (default yyyy-MM-dd)
* business.weekend.days - specifies days of the weekend (default Saturday and Sunday i.e 6,7)

### Example test results without calendar.properties (working hours)
* The timer for the Verify Payment task will follow a straightforward countdown based on real time. If the specified time elapses i.e., 1 second, it immediately moves to cancel payment task.


* POST/ BusinessCalendarCreditBill
```sh
curl -X POST http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-d '{"creditCardNumber": null, "creditCardDetails": {"cardNumber": "434353433", "status": "Bill Due"}}'

```
<p align="center"><img width=75% height=50% src="docs/images/Post1.png"></p>

* After 1 second when we send request for GET/ BusinessCalendarCreditBill again we get empty array representing the cancellation.
```sh
curl -X GET http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-H "Accept: application/json"

```
<p align="center"><img width=75% height=50% src="docs/images/Get1.png"></p>

### Example of logs representing the process from start to active
<p align="center"><img width=75% height=50% src="docs/images/WithPropertiesLogs.png"></p>


* The workflow 'BusinessCalendarCreditBill' began at 08:11:58,353 with a unique identifier 130f2eab-ab2e-413d-958e-414d1b3b0dc7.

* At 08:11:58,364, the node 'Start' was triggered.

* The subsequent node 'Process Credit Bill' was activated at 08:11:58,368.

* The 'Verify Payment' node was triggered at 08:11:58,397.

* A human task was registered at 08:11:58,444.

* The workflow transitioned to an 'Active' state at 08:11:58,496.

* Importantly, the log at 08:11:59,406 indicates that the job e3646ed7-76bf-498c-aa72-7086f4b847c6 was started, emphasizing that the timer was triggered after 1 second as anticipated.

* The 'Cancel Payment' node triggered at 08:11:59,425. CreditCardService logged the bill cancellation. 

* The final node 'End' was initiated at 08:11:59,430. The workflow completed successfully at 08:11:59,478.

* Hence, without calendar.properties, timer fires immediately after their configured interval, activating tasks without delay.

### Example test results without calendar.properties (non-working hours)
* During non-working hours, the timer for the Verify Payment task will not trigger and the process remains in active state, does not move to cancel payment task.

* POST/ BusinessCalendarCreditBill
```sh
curl -X POST http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-d '{"creditCardNumber": null, "creditCardDetails": {"cardNumber": "434353433", "status": "Bill Due"}}'
```
<p align="center"><img width=75% height=50% src="docs/images/Post3.png"></p>


* GET/ BusinessCalendarCreditBill
```sh
curl -X GET http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-H "Accept: application/json"

```
* Now, even after 1 second, the process will be in Active State.

<p align="center"><img width=75% height=50% src="docs/images/Get3.png"></p>

### Example of logs representing the active state during non-working hours

<p align="center"><img width=75% height=50% src="docs/images/WithoutPropertiesLogsNW.png"></p>


## Testing with calendar.properties (During non-working hours/Specified Holiday)
### Steps to create & configure calendar.properties with a holiday/non-working hours

* Based on requirement, create/Modify calendar.properties file in the src/main/resources directory. This file activates the Business Calendar feature and essential configurations

### calendar.properties format
* business.days.per.week - specifies number of working days per week 
* business.hours.per.day - specifies number of working hours per day 
* business.start.hour - specifies starting hour of work day 
* business.end.hour - specifies ending hour of work day 
* business.holidays - specifies holidays in yyy-MM-dd format
* business.holiday.date.format - specifies holiday date format used 
* business.weekend.days - specifies days of the weekend (If weekend has to considered as working days, consider a value out of range 1-7, i.e. 8)
* business.cal.timezone - system default timezone

```properties
business.end.hour=24
business.hours.per.day=24
business.start.hour=0
business.holiday.date.format=yyyy-MM-dd
business.holidays=2024-11-07
business.days.per.week =7
business.weekend.days = 8
#business.cal.timezone= system default timezone
```

* After calendar.properties file is added, build the example again "mvn clean compile quarkus:dev" or type 's' in the quarkus terminal and hit enter just to restart.

* POST/ BusinessCalendarCreditBill
```sh
curl -X POST http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-d '{"creditCardNumber": null, "creditCardDetails": {"cardNumber": "434353433", "status": "Bill Due"}}'
```
<p align="center"><img width=75% height=50% src="docs/images/Post2.png"></p>


* GET/ BusinessCalendarCreditBill
```sh
curl -X GET http://localhost:8080/BusinessCalendarCreditBill \
-H "Content-Type: application/json" \
-H "Accept: application/json"

```
* Now, even after 1 second, the process will be in Active State.

<p align="center"><img width=75% height=50% src="docs/images/Get2.png"></p>

### Example of logs representing the active state during non-working hours/specified holiday

<p align="center"><img width=75% height=50% src="docs/images/WithPropertiesLogs.png"></p>

* The node 'Start' for the process 'BusinessCalendarCreditBill', identified by 08ea5258-9d91-4f05-a8d8-184107c042ed, was triggered at 08:54:28,621.

* At 08:54:28,629, the 'Process Credit Bill' node was activated.

* At 08:54:28,653, verification step through the 'Verify Payment' node was started.

* Subsequently, a human task was registered at 08:54:28,773.

* The workflow transitioned to an 'Active' state at 08:54:28,808.

* Due to mentioned "business.holidays property" in calendar.properties, timer does not trigger and the state remains active.

* On next business day, timer will resume at the beginning of the next working hour/day, after the non-working hour/holiday has ended. The timer is set to fire after one second of active business time.

