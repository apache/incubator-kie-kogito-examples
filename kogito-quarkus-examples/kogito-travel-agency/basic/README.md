# Kogito Travel Agency - base version

## Description

During this workshop we will create a software system for a startup travel agency called Kogito Travel Agency. The first iteration of the system will consist of a set of services that are able to deal with travel requests and the booking of hotels and flights.

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
* Create service classes  
  * HotelBookingService
  * FlightBookingService
* Create decision logic
  * Visa check
* Create business logic
  * Public business process to deal with complete travel request
  * Private business process to deal with hotel booking
  * Private business process to deal with flight booking
* Create a test case that makes use of processes and decisions
* Create or import UI components
* Add metrics support for processes and decisions
* Create dashboard based on metrics

## Data model

Kogito Travel Agency booking system will be based on following data model

### Traveller

A person who requests a new travel

### Trip

Place/Location where the traveller wants to go and dates

### Flight

Flight that has been booked for the traveller to take him/her to the destination

### Hotel

Place/Location where the traveller will stay during his/her travel

### Address

Location that is associated with either traveller or hotel

<p align="center"><img width=75%  src="docs/images/datamodel.png"></p>

## Decision logic

The decision logic will be implemented as a decision table. The logic will be responsible for verifying whether a given traveller requires a visa to enter a given country or not. The decision logic reason over the following data/facts

* Destination that the traveller wants to go - country
* Nationality of the traveller
* Length of the stay

The result will be “yes” or “no”.

<p align="center"><img width="100%" src="docs/images/decisiontable.png"></p>

## Business logic

Business logic will be based on business processes

Public process that will be responsible for orchestrating complete travel request

<p align="center"><img width=100% src="docs/images/travels-process.png"></p>

Private process that will be responsible for booking a hotel.

<p align="center"><img width=50% src="docs/images/book-hotel-process.png"></p>

Private process that will be responsible for booking a flight.

<p align="center"><img width=50% src="docs/images/book-flight-process.png"></p>

## Services

There will be services implemented to carry on the hotel and flight booking. Implementation will be a CDI beans that will have hard coded logic to return a booked flight or hotel.

* org.acme.travels.service.HotelBookingService
* org.acme.travels.service.FlightBookingService

## Try out the complete service

### Installing and Running

#### Prerequisites

You will need:

* Java 17+ installed
* Environment variable JAVA_HOME set accordingly
* Maven 3.9.6+ installed

When using native image compilation, you will also need:

* GraalVM 20 installed
* Environment variable GRAALVM_HOME set accordingly
* Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

#### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules and decision
tables and java code. No need to redeploy or restart your running application.

#### Compile and Run using Local Native Image

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```sh
./target/kogito-travel-agency-{version}-runner
```

## User interface

Kogito Travel Agency comes with basic UI that allows to

### Plan new trips

<p align="center"><img width=75% height=75% src="docs/images/new-trip.png"></p>

### List currently opened travel requests

<p align="center"><img width=75% height=75% src="docs/images/list-trips.png"></p>

### Show details of selected travel request

<p align="center"><img width=75% height=75% src="docs/images/trip-details.png"></p>

### Show active tasks of selected travel request

<p align="center"><img width=75% height=75% src="docs/images/tasks.png"></p>

### Cancel selected travel request

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

As response a single travel request is returned if found, otherwise 404 Not Found is returned.

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

Complete visa application task by sending a valid URL to the VISA document stored in any cloud provider:

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8080/travels/{uuid}/VisaApplication/{task-uuid} -d '{"visaApplication": "https://mydrive.example.com/JanVisaApplicationForm.pdf"}'
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

## Known issues
