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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.domain.FlightDTO;
import org.kie.kogito.examples.domain.Passenger;
import org.kie.kogito.examples.domain.PassengerDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class FlightTest {

    @Test
    public void testFlightsProcess() throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();

        FlightDTO flightParams = new FlightDTO();
        flightParams.setDepartureDateTime(LocalDateTime.now().toString());
        flightParams.setDestination("B");
        flightParams.setOrigin("A");
        flightParams.setSeatColumnSize(10);
        flightParams.setSeatRowSize(6);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("params", flightParams);

        // ProcessInstance<?> processInstance = process.createInstance(m);
        String id = given()
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
                .get("/rest/flights")
                .then()
                .statusCode(200)
                .body(containsString(id));

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

        // add new passenger
        PassengerDTO passengerDTO = new PassengerDTO("john", "WINDOW", false, false, null);
        given()
                .body(jsonMapper.writeValueAsString(passengerDTO))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + id + "/newPassengerRequest")
                .then()
                .statusCode(200);

        Map<String, String> tasks = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id + "/tasks")
                .then()
                .extract()
                .body()
                .jsonPath()
                .getMap("");
        assertEquals(2, tasks.size());
        assertThat(tasks.values(), hasItem("approveDenyPassenger"));
        assertThat(tasks.values(), hasItem("finalizePassengerList"));

        String approveDenyPassengerTask = tasks.entrySet().stream()
                .filter(e -> e.getValue().equals("approveDenyPassenger"))
                .map(Map.Entry::getKey).findAny().get();

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id + "/approveDenyPassenger/" + approveDenyPassengerTask)
                .then()
                .statusCode(200)
                .body("passenger.name", is(passengerDTO.getName()));

        // approve passenger
        parameters = new HashMap<>();
        parameters.put("isPassengerApproved", true);
        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + id + "/approveDenyPassenger/" + approveDenyPassengerTask)
                .then()
                .statusCode(200);

        // close the passenger list so no more passengers can be added
        String finalizePassengerListTask = tasks.entrySet().stream()
                .filter(e -> e.getValue().equals("finalizePassengerList"))
                .map(Map.Entry::getKey).findAny().get();
        parameters = new HashMap<>();
        parameters.put("isPassengerListFinalized", true);
        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + id + "/finalizePassengerList/" + finalizePassengerListTask)
                .then()
                .statusCode(200);

        try {
            Thread.sleep(10000);
        } catch (Exception e) {

        }

        tasks = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id + "/tasks")
                .then()
                .extract()
                .body()
                .jsonPath()
                .getMap("");

        assertEquals(1, tasks.size());
        // Verify flight is assigned
        List<Passenger> passengerList = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("flight.passengerList", Passenger.class);

        assertNotNull(passengerList.get(0).getSeat());

        // then complete the flight
        String finializeSeatAssignmentTask = tasks.keySet().iterator().next();
        parameters = new HashMap<>();

        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + id + "/finalizeSeatAssignment/" + finializeSeatAssignmentTask)
                .then()
                .statusCode(200);

        // Assert there are no new flights
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
                .get("/rest/flights/" + id)
                .then()
                // I would expect a 404, not a 204, for a missing process
                .statusCode(204);
    }
}
