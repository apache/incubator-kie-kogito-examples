# jBPM + OptaPlanner + Quarkus example

## Description

A simple process service for an airline scheduling flights and optimizing seat
assignments using OptaPlanner.

It uses an event-based subprocess to simulate
passengers buying tickets, who the airline security officer must approve via
a user task before the passenger is allowed to get a ticket.

The main process waits for a user task to be completed to finalize the passenger list.
It then invokes a custom Java service `FlightSeatingSolveService.assignSeats`
to optimize the flight's seats following the rules in `FlightSeatingConstraintProvider`
using OptaPlanner's, followed by a user task to finalize seat assignments.

Based on these two processes (defined using BPMN 2.0 format), the custom data object
and custom Java service, a new service is generated that exposes REST operations to
create new flights (following the steps as defined in the main and sub-process)
and add passengers to flights.

A UI is included in this example, which can be accessed on `localhost:8080`
when the application is running.

## Installing and Running

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed
  
When using native image compilation, you will also need: 
  - [GraalVM 19.1.1](https://github.com/oracle/graal/releases/tag/vm-19.1.1) installed 
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too.  You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites) for more details.

## Installing and Running

```
mvn clean package quarkus:dev 
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```
./target/jbpm-optaplanner-quarkus-example-{version}-runner
```

Note: This does not yet work on Windows, GraalVM and Quarkus should be rolling out support for Windows soon

## Swagger documentation

You can take a look at the [swagger definition](http://localhost:8080/openapi) - automatically generated and included in
this service - to determine all available operations exposed by this service.  For easy readability you can visualize
the swagger definition file using a swagger UI like for example available [here](https://editor.swagger.io).
In addition, various clients to interact with this service can be easily generated using this swagger definition.

## Example Usage

Once the service is up and running, you can access the UI at `localhost:8080` or use
the following examples to interact with the service.

### POST  /rest/flights

Allows to create a new flight with the given data:

```sh
curl -d '{ "params": { "origin" : "JFK", "destination": "SFO", "departureDateTime": "2020-01-01T12:00", "seatRowSize": 8, "seatColumnSize": 6 } }' -H "Content-Type: application/json" -X POST http://localhost:8080/rest/flights
```
or on windows

```sh
curl -d "{\"params\": { \"origin\" : \"JFK\", \"destination\": \"SFO\", \"departureDateTime\": \"2020-01-01T12:00\", \"seatRowSize\": 8, \"seatColumnSize\": 6 }}" -H "Content-Type: application/json" -X POST http://localhost:8080/rest/flights
```

As response the created flight is returned (in field "flight").
Example response:

```json
{
  "id":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "flight":{
    "flightInfo":{
      "origin":"YYZ",
      "destination":"KRND",
      "departureDateTime":{...},
      "seatRowSize":1,
      "seatColumnSize":1
      },
    "seatList":[{"name":"1A","row":0,"column":0,"seatType":"WINDOW","emergencyExitRow":false}],
    "passengerList":[],
    "score":null,
    "origin":"YYZ",
    "seatColumnSize":6,
    "seatRowSize":4,
    "destination":"KRND",
    "departureDateTime":{...}
    },
  "isSolving":true,
  "processId":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "params":{
    "origin":"YYZ",
    "destination":"KRND",
    "departureDateTime":"2020-03-06T20:19:49.240",
    "seatRowSize":4,
    "seatColumnSize":6
  },
  "isPassengerListFinalized":false
}
```

### GET  /rest/flights

Returns list of flights currently being scheduled:

```sh
curl -X GET http://localhost:8080/rest/flights
```

As response an array of flights is returned.

### GET  /rest/flight/{id}

Returns flight with given id (if being scheduled):

```sh
# Replace {id} with the process id
curl -X GET http://localhost:8080/rest/flights/{id}
```

As response a single flight is returned if found, otherwise no content (204) is returned.

### DELETE /rest/flights/{id}

Cancels flight with given id

```sh
# Replace {id} with the process id
curl -X DELETE http://localhost:8080/rest/flights/{id}
```

### GET /rest/flights/{id}/tasks

Get user tasks that currently require action for a flight (with task id's as keys, and task types as values).

```sh
# Replace {id} with the process id
curl -X GET http://localhost:8080/rest/flights/{id}/tasks
```

Example response:

```json
{
  "66c11e3e-c211-4cee-9a07-848b5e861bc5": "finalizePassengerList",
  "a2c11e3e-c211-4cee-9a07-848b5e861bc5": "finalizeSeatAssignment",
  "f4c11e3e-c211-4cee-9a07-848b5e861bc5": "approveDenyPassenger"
}
```

### POST /rest/flights/{id}/newPassengerRequest

Create a new Ticket request for a passenger, who must be approved by security.

```sh
# Replace {id} with the process id
curl -d '{ "passenger": { "name": "Amy Cole", "seatTypePreference": "WINDOW", "emergencyExitRowCapable": true, "paidForSeat": true, "seat": "3A" } }' -X POST http://localhost:8080/rest/flights/{id}/newPassengerRequest
```
or on Windows:

```sh
rem Replace {id} with the process id
curl -d "{ \"passenger\": { \"name\": \"Amy Cole\", \"seatTypePreference\": \"WINDOW\", \"emergencyExitRowCapable\": true, \"paidForSeat\": true, \"seat\": \"3A\" } }" -X POST http://localhost:8080/rest/flights/{id}/newPassengerRequest
```

### GET /rest/flights/{id}/approveDenyPassenger/{workItemId}

Get the passenger that need to be denied or approved for the given "approveDenyPassenger" task.

```sh
# Replace {id} with the process id and {taskId} with the id of the task to complete
curl -X GET http://localhost:8080/rest/flights/{id}/approveDenyPassenger/{taskId}
```
Example response:

```json
{
  "passenger": {
    "name": "Amy Cole",
    "seatTypePreference": "WINDOW",
    "emergencyExitRowCapable": true,
    "paidForSeat": true,
    "seat": "3A"
  }
}
```

### POST /rest/flights/{id}/approveDenyPassenger/{workItemId}

Approves an passenger and add them to the flight if "isPassengerApproved" is true, otherwise cancels their ticket.

```sh
# Replace {id} with the process id and {taskId} with the id of the task to complete
curl -d '{ "isPassengerApproved": true }' -X POST http://localhost:8080/rest/flights/{id}/approveDenyPassenger/{taskId}
```
or in Windows

```sh
rem Replace {id} with the process id and {taskId} with the id of the task to complete
curl -d "{ \"isPassengerApproved\": true }" -X POST http://localhost:8080/rest/flights/{id}/approveDenyPassenger/{taskId}
```

Example Response:

```json
{
  "id":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "flight":{
    "flightInfo":{
      "origin":"YYZ",
      "destination":"KRND",
      "departureDateTime":{...},
      "seatRowSize":1,
      "seatColumnSize":1
      },
    "seatList":[{"name":"1A","row":0,"column":0,"seatType":"WINDOW","emergencyExitRow":false}],
    "passengerList":[{
      "id": 0,
      "name": "Amy Cole",
      "seatTypePreference": "WINDOW",
      "emergencyExitRowCapable": true,
      "paidForSeat": false,
      "seat":null
    }],
    "score":null,
    "origin":"YYZ",
    "seatColumnSize":6,
    "seatRowSize":4,
    "destination":"KRND",
    "departureDateTime":{...}
    },
  "isSolving":true,
  "processId":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "params":{
    "origin":"YYZ",
    "destination":"KRND",
    "departureDateTime":"2020-03-06T20:19:49.240",
    "seatRowSize":4,
    "seatColumnSize":6
  },
  "isPassengerListFinalized":false
}
```

### POST /rest/flights/{id}/finalizePassengerList/{workItemId}

Finalize the passenger list for the flight.

```sh
# Replace {id} with the process id and {taskId} with the id of the task to complete
curl -d '{}' -X POST http://localhost:8080/rest/flights/{id}/finalizePassengerList/{taskId}
```

Example response:

```json
{
  "id":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "flight":{
    "flightInfo":{
      "origin":"YYZ",
      "destination":"KRND",
      "departureDateTime":{...},
      "seatRowSize":1,
      "seatColumnSize":1
      },
    "seatList":[{"name":"1A","row":0,"column":0,"seatType":"WINDOW","emergencyExitRow":false}],
    "passengerList":[{
      "id": 0,
      "name": "Amy Cole",
      "seatTypePreference": "WINDOW",
      "emergencyExitRowCapable": true,
      "paidForSeat": false,
      "seat":null
    }],
    "score":null,
    "origin":"YYZ",
    "seatColumnSize":6,
    "seatRowSize":4,
    "destination":"KRND",
    "departureDateTime":{...}
    },
  "isSolving":true,
  "processId":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "params":{
    "origin":"YYZ",
    "destination":"KRND",
    "departureDateTime":"2020-03-06T20:19:49.240",
    "seatRowSize":4,
    "seatColumnSize":6
  },
  "isPassengerListFinalized":true
}
```

### POST /rest/flights/{id}/finalizeSeatAssignment/{workItemId}

Finalize seat assignments for the flight.

```sh
# Replace {id} with the process id and {taskId} with the id of the task to complete
curl -d '{}' -X POST http://localhost:8080/rest/flights/{id}/finalizeSeatAssignment/{taskId}
```

Example response:

```json
{
  "id":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "flight":{
    "flightInfo":{
      "origin":"YYZ",
      "destination":"KRND",
      "departureDateTime":{...},
      "seatRowSize":1,
      "seatColumnSize":1
      },
    "seatList":[{"name":"1A","row":0,"column":0,"seatType":"WINDOW","emergencyExitRow":false}],
    "passengerList":[{
      "id": 0,
      "name": "Amy Cole",
      "seatTypePreference": "WINDOW",
      "emergencyExitRowCapable": true,
      "paidForSeat": false,
      "seat":{"name":"1A","row":0,"column":0,"seatType":"WINDOW","emergencyExitRow":false}
    }],
    "score":null,
    "origin":"YYZ",
    "seatColumnSize":6,
    "seatRowSize":4,
    "destination":"KRND",
    "departureDateTime":{...}
    },
  "isSolving":false,
  "processId":"7f24831f-9dc6-44c7-8dec-9b4a696506b5",
  "params":{
    "origin":"YYZ",
    "destination":"KRND",
    "departureDateTime":"2020-03-06T20:19:49.240",
    "seatRowSize":4,
    "seatColumnSize":6
  },
  "isPassengerListFinalized":true
}
```
