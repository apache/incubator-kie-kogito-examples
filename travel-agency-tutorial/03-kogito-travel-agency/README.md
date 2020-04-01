# Kogito Travel Agency


## Description

We keep working in the startup travel agency called Kogito Travel Agency. After the  creation of a set of services that are 
able to deal with travel requests and the booking of hotels and flights, we added runtime workflow persistence. 
This time, we are going to add communication between services: this one **Kogito travel service** that deal with travels details and, 
in case visa is required for the traveller, she will be given a chance to apply for visa and by that send visa application 
automatically to **Kogito Visas service** explained in the next step of the tutorial.

## Activities to perform

* Create project using Quarkus Maven plugin with following extensions
	* Kogito
	* OpenApi
* Import project into Eclipse IDE - requires BPMN modeller plugin installed
* Create data model
	* Traveller
	* Hotel
	* Flight
	* Address
	* Trip
	* VisaApplication
* Create service classes
	* HotelBookingService
	* FlightBookingService
* Create decision logic
	* Visa check
* Create business logic
	* Public business process to deal with complete travel request
	* Private business process to deal with hotel booking
	* Private business process to deal with flight booking
* Enable runtime Persistence for runtime workflow
* Create a test case that makes use of processes and decisions
* Configure messaging and events
* Create or import UI components
* Add metrics support for processes and decisions
* Create dashboard based on metrics

## Data model

Kogito Travel Agency booking system will be based on following data model

**Traveller**

A person who requests a new travel

**Trip**

Place/Location where the traveller wants to go and dates

**Flight**

Flight that has been booked for the traveller to take him/her to the destination

**Hotel**

Place/Location where the traveller will stay during his/her travel

**Address**

Location that is associated with either traveller or hotel

<p align="center"><img width=75% height=75% src="docs/images/datamodel.png"></p>


## Decision logic

The decision logic will be implemented as a decision table. The logic will be responsible for verifying whether a given traveller requires a visa to enter a given country or not. The decision logic reason over the following data/facts

* Destination that the traveller wants to go - country
* Nationality of the traveller
* Length of the stay

The result will be “yes” or “no”.

<p align="center"><img width=100% src="docs/images/decisiontable.png"></p>


## Business logic

Business logic will be based on business processes

Public process that will be responsible for orchestrating complete travel request

<p align="center"><img width=100% src="docs/images/travels-process.png"></p>

Private process that will be responsible for booking a hotel.

<p align="center"><img width=50%  src="docs/images/book-hotel-process.png"></p>

Private process that will be responsible for booking a flight.

<p align="center"><img width=50% src="docs/images/book-flight-process.png"></p>

## Services

There will be services implemented to carry on the hotel and flight booking. Implementation will be a CDI beans that will have hard coded logic to return a booked flight or hotel.

* org.acme.travels.service.HotelBookingService
* org.acme.travels.service.FlightBookingService



# Try out the complete service

## Installing and Running

### Prerequisites

You will need:
  - Java 1.8.0+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

When using native image compilation, you will also need:
  - GraalVM installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Infrastructure requirements

#### Infinispan

This application requires an Infinispan server to be available and by default expects it to be on default port and localhost.

You can install Infinispan server by downloading it from [official website](https://infinispan.org/download) version to be used in 10.0.0.CR2
Here  [https://github.com/kiegroup/kogito-runtimes/wiki/Persistence](https://github.com/kiegroup/kogito-runtimes/wiki/Persistence) the required 
Infinispan configuration is explained in more detail.

Alternatively, you can use the Docker Compose template, instructions on how to use it are available in the [README](../docker-compose/README.md) file.

#### Apache Kafka

This application requires a [Apache Kafka](https://kafka.apache.org/) installed and following topics created

* `visaapplications` - used to send visa application that are consumed and processed by Kogito Visas service
* `kogito-processinstances-events` - used to emit events by kogito that can be consumed by data index service and other services
* `kogito-usertaskinstances-events` -used to emit events by kogito that can be consumed by data index service

Alternatively, you can use the Docker Compose template, instructions on how to use it are available in the [README](../docker-compose/README.md) file.

### Compile and Run in Local Dev Mode

```
mvn clean package quarkus:dev    
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules and decision
tables and java code. No need to redeploy or restart your running application.


### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/kogito-travel-agency-{version}-runner
```

## Known issues


## User interface

Kogito Travel Agency comes with basic UI that allows to

### plan new trips

<p align="center"><img width=75% height=75% src="docs/images/new-trip.png"></p>

### list currently opened travel requests

<p align="center"><img width=75% height=75% src="docs/images/list-trips.png"></p>

### show details of selected travel request

<p align="center"><img width=75% height=75% src="docs/images/trip-details.png"></p>

### show active tasks of selected travel request

<p align="center"><img width=75% height=75% src="docs/images/tasks.png"></p>

### perform Human task: visa application

<p align="center"><img width=75% height=75% src="docs/images/visa-application.png"></p>

### cancel selected travel request

To start Kogito Travel Agency UI just point your browser to [http://localhost:8080](http://localhost:8080)

## REST API

Once the service is up and running, you can use the following examples to interact with the service.

### POST /travels

Send travel that requires does not require visa

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8080/travels -d @- << EOF
{
	"traveller" : {
		"firstName" : "John",
		"lastName" : "Doe",
		"email" : "john.doe@example.com",
		"nationality" : "American",
		"address" : {
			"street" : "main street",
			"city" : "Boston",
			"zipCode" : "10005",
			"country" : "US"
		}
	},
	"trip" : {
		"city" : "New York",
		"country" : "US",
		"begin" : "2019-12-10T00:00:00.000+02:00",
		"end" : "2019-12-15T00:00:00.000+02:00"
	}
}
EOF

```

This will directly go to 'ConfirmTravel' user task.

Send travel request that requires does require visa

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8080/travels -d @- << EOF
{
	"traveller" : {
		"firstName" : "Jan",
		"lastName" : "Kowalski",
		"email" : "jan.kowalski@example.com",
		"nationality" : "Polish",
		"address" : {
			"street" : "polna",
			"city" : "Krakow",
			"zipCode" : "32000",
			"country" : "Poland"
		}
	},
	"trip" : {
		"city" : "New York",
		"country" : "US",
		"begin" : "2019-12-10T00:00:00.000+02:00",
		"end" : "2019-12-15T00:00:00.000+02:00"
	}
}
EOF
```

This will stop at 'VisaApplication' user task.

### GET /travels

Returns list of travel requests currently active:

```sh
curl -X GET http://localhost:8080/travels
```

As response an array of travels is returned.

### GET /travels/{id}

Returns travel request with given id (if active):

```sh
curl -X GET http://localhost:8080/travels/{uuid}
```

As response a single travel request is returned if found, otherwise no content (204) is returned.

### DELETE /travels/{id}

Cancels travel request with given id

```sh
curl -X DELETE http://localhost:8080/travels/{uuid}
```

### GET /travels/{id}/tasks

Returns currently assigned user tasks for give travel request:

```sh
curl -X GET http://localhost:8080/travels/{uuid}/tasks
```

### GET /travels/{id}/VisaApplication/{taskId}

Returns visa application task information:

```sh
curl -X GET http://localhost:8080/travels/{uuid}/VisaApplication/{task-uuid}
```

### POST /travels/{id}/VisaApplication/{taskId}

Completes visa application task

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8080/travels/{uuid}/VisaApplication/{task-uuid} -d @- << EOF
{
	"visaApplication" : {
		"firstName" : "Jan",
		"lastName" : "Kowalski",
		"nationality" : "Polish",
		"city" : "New York",
		"country" : "US",
		"passportNumber" : "ABC09876",
		"duration" : 25
	}
}
EOF
```

### GET /travels/{id}/ConfirmTravel/{taskId}

Returns travel (hotel, flight) task information required for confirmation:

```sh
curl -X GET http://localhost:8080/travels/{uuid}/ConfirmTravel/{task-uuid}
```

### POST /travels/{id}/ConfirmTravel/{taskId}

Completes confirms travel task - meaning confirms (and completes) the travel request

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8080/travels/{uuid}/ConfirmTravel/{task-uuid} -d '{}'
```
