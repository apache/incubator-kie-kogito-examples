/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.examples.quarkus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.domain.FlightDTO;
import org.kie.kogito.examples.domain.Passenger;
import org.kie.kogito.examples.domain.PassengerDTO;
import org.kie.kogito.process.WorkItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
                .statusCode(201)
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

        WorkItem[] tasks = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id + "/tasks")
                .then()
                .statusCode(200)
                .body("$.size", is(2))
                .extract().as(DefaultWorkItem[].class);

        String denyId = findIdByName(tasks, "approveDenyPassenger");
        assertNotNull(denyId, "returned task does not contain approveDenyPassenger");
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id + "/approveDenyPassenger/" + denyId)
                .then()
                .statusCode(200)
                .body("parameters.passenger.name", is(passengerDTO.getName()));

        // approve passenger
        parameters = new HashMap<>();
        parameters.put("isPassengerApproved", true);
        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + id + "/approveDenyPassenger/" + denyId)
                .then()
                .statusCode(200);

        // close the passenger list so no more passengers can be added
        String finalizeId = findIdByName(tasks, "finalizePassengerList");
        assertNotNull(denyId, "returned task does not contain finalizePassengerList");

        parameters = new HashMap<>();
        parameters.put("isPassengerListFinalized", true);
        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + id + "/finalizePassengerList/" + finalizeId)
                .then()
                .statusCode(200);

        try {
            Thread.sleep(10000);
        } catch (Exception e) {

        }

        String taskId = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/rest/flights/" + id + "/tasks")
                .then()
                .statusCode(200)
                .body("$.size", is(1))
                .body("[0].name", is("finalizeSeatAssignment"))
                .extract()
                .path("[0].id");

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

        parameters = new HashMap<>();

        given()
                .body(jsonMapper.writeValueAsString(parameters))
                .contentType(ContentType.JSON)
                .when()
                .post("/rest/flights/" + id + "/finalizeSeatAssignment/" + taskId)
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
                .statusCode(404);
    }

    private String findIdByName(WorkItem[] tasks, String taskName) {
        for (WorkItem task : tasks) {
            if (taskName.equals(task.getName()))
                return task.getId();
        }
        return null;
    }
}
