/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.examples;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.examples.domain.FlightDTO;
import org.kie.kogito.examples.domain.Passenger;
import org.kie.kogito.examples.domain.PassengerDTO;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FlightSeatingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FlightTest {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void runProcess() throws JsonProcessingException {
        FlightDTO flightParams = new FlightDTO();
        flightParams.setDepartureDateTime(LocalDateTime.now().toString());
        flightParams.setDestination("B");
        flightParams.setOrigin("A");
        flightParams.setSeatColumnSize(10);
        flightParams.setSeatRowSize(6);

        String flightId = createFlight(flightParams);

        PassengerDTO passenger = new PassengerDTO("john", "WINDOW", false, false, null);
        addPassenger(flightId, passenger);

        Map<String, String> tasks = getTasks(flightId);
        assertEquals(2, tasks.size());
        assertThat(tasks.values(), hasItem("approveDenyPassenger"));
        assertThat(tasks.values(), hasItem("finalizePassengerList"));

        String approveDenyPassengerTask = tasks.entrySet().stream()
                .filter(e -> e.getValue().equals("approveDenyPassenger"))
                .map(Map.Entry::getKey).findAny().get();

        approvePassenger(flightId, approveDenyPassengerTask, passenger.getName());

        // close the passenger list so no more passengers can be added
        String finalizePassengerListTask = tasks.entrySet().stream()
                .filter(e -> e.getValue().equals("finalizePassengerList"))
                .map(Map.Entry::getKey).findAny().get();

        finalizePassengerList(flightId, finalizePassengerListTask);

        String finalizeSeatAssignmentTask = waitForNextTaskAfterAssignment(flightId, 10);

        verifyFlightIsAssigned(flightId);

        finalizeSeatAssignment(flightId, finalizeSeatAssignmentTask);

        assertNotMoreFlights(flightId);
    }

    private String waitForNextTaskAfterAssignment(String flightId, int timeoutSeconds) {
        final long stepMillis = 1000L;
        long waitingSpentMillis = 0L;
        Map<String, String> tasks;

        while((tasks = getTasks(flightId)).isEmpty()) {
            if (waitingSpentMillis > timeoutSeconds * 1000) {
                throw new RuntimeException("Waiting for seat assignment has exceeded " + timeoutSeconds + " seconds (timeout).");
            }

            waitingSpentMillis += stepMillis;
            try {
                Thread.sleep(stepMillis);
            } catch (Exception e) {
                System.out.println("Interrupted waiting.");
                e.printStackTrace();
            }
        }

        return tasks.keySet().iterator().next();
    }

    private String createFlight(FlightDTO flightParams) throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("params", flightParams);

        final String id = given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights")
                .then()
                .statusCode(200)
                .body("params.departureDateTime", is(flightParams.getDepartureDateTime()))
                .body("params.origin", is(flightParams.getOrigin()))
                .body("params.destination", is(flightParams.getDestination()))
                .body("params.seatRowSize", is(flightParams.getSeatRowSize()))
                .body("params.seatColumnSize", is(flightParams.getSeatColumnSize()))
                .extract()
                .body()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id)
                .then()
                .statusCode(200)
                .body("params.departureDateTime", is(flightParams.getDepartureDateTime()))
                .body("params.origin", is(flightParams.getOrigin()))
                .body("params.destination", is(flightParams.getDestination()))
                .body("params.seatRowSize", is(flightParams.getSeatRowSize()))
                .body("params.seatColumnSize", is(flightParams.getSeatColumnSize()));

        return id;
    }

    private void addPassenger(String flightId, PassengerDTO passenger) throws JsonProcessingException {
        given()
                .body(jsonMapper.writeValueAsString(passenger))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + flightId + "/newPassengerRequest")
                .then()
                .statusCode(200);
    }

    private Map<String, String> getTasks(String flightId) {
        Map<String, String> tasks = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + flightId + "/tasks")
                .then()
                .extract()
                .body()
                .jsonPath()
                .getMap("");

        return tasks;
    }

    private void approvePassenger(String flightId, String approveDenyPassengerTask, String passengerName)
            throws JsonProcessingException {
        // check the passenger name
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + flightId + "/approveDenyPassenger/" + approveDenyPassengerTask)
                .then()
                .statusCode(200)
                .body("passenger.name", is(passengerName));

        // approve passenger
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("isPassengerApproved", true);
        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + flightId + "/approveDenyPassenger/" + approveDenyPassengerTask)
                .then()
                .statusCode(200);
    }

    private void finalizePassengerList(String flightId, String finalizePassengerListTask) throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("isPassengerListFinalized", true);
        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + flightId + "/finalizePassengerList/" + finalizePassengerListTask)
                .then()
                .statusCode(200);
    }

    private void verifyFlightIsAssigned(String flightId) {
        List<Passenger> passengerList = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + flightId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("flight.passengerList", Passenger.class);

        Assertions.assertNotNull(passengerList.get(0).getSeat());
    }

    private void finalizeSeatAssignment(String flightId, String finializeSeatAssignmentTask) throws JsonProcessingException  {
        given()
                .body(jsonMapper.writeValueAsString(Collections.EMPTY_MAP))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + flightId + "/finalizeSeatAssignment/" + finializeSeatAssignmentTask)
                .then()
                .statusCode(200);
    }

    private void assertNotMoreFlights(String flightId) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights")
                .then()
                .statusCode(200)
                .body("", hasSize(0));

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + flightId)
                .then()
                // I would expect a 404, not a 204, for a missing process
                .statusCode(204);
    }
}
