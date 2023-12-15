/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme.travel;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.travels.Address;
import org.acme.travels.Flight;
import org.acme.travels.Hotel;
import org.acme.travels.Traveller;
import org.acme.travels.Trip;
import org.acme.travels.VisaApplication;
import org.acme.travels.VisaResolution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;

@QuarkusTest
@QuarkusTestResource(value = InfinispanQuarkusTestResource.class)
@QuarkusTestResource(value = KafkaQuarkusTestResource.class)
public class TravelIT {

    private static final Traveller TRAVELLER_FROM_POLAND = new Traveller("Jan", "Kowalski", "jan.kowalski@example.com", "Polish", "N7397478", new Address("polna", "Krakow", "32000", "Poland"));
    private static final Trip TRIP_TO_POLAND = new Trip("Another City", "Poland", new Date(), new Date());
    private static final Trip TRIP_TO_US = new Trip("New York", "US", new Date(), new Date());

    private static final String STEP_CONFIRM_TRAVEL = "ConfirmTravel";
    private static final String STEP_VISA_APPLICATION = "VisaApplication";
    private static final String PROJECT_VERSION = ProjectMetadataProvider.getProjectVersion();
    private static final String PROJECT_ARTIFACT_ID = ProjectMetadataProvider.getProjectArtifactId();

    private static final SecurityPolicy SECURITY_POLICY_JDOE = SecurityPolicy.of(IdentityProviders.of("jdoe", Collections.emptyList()));

    @Inject
    @Named("travels")
    Process<? extends Model> travelsProcess;

    private ProcessInstance<?> processInstance;

    @BeforeEach
    public void cleanUp() {
        abort(travelsProcess.instances());
        processInstance = null;
    }

    @Test
    public void testTravelNoVisaRequired() {
        whenNewTravel(TRAVELLER_FROM_POLAND, TRIP_TO_POLAND);
        thenProcessIsActive();
        thenHotelAndFlightAreChosen();

        whenConfirmTravel();
        thenProcessIsCompleted();
    }

    @Test
    public void testTravelVisaRequired() {
        whenNewTravel(TRAVELLER_FROM_POLAND, TRIP_TO_US);
        thenProcessIsActive();

        whenAddVisaApplication();
        thenProcessIsActive();
    }

    @Test
    public void testProcessMetrics() {
        whenNewTravel(TRAVELLER_FROM_POLAND, TRIP_TO_POLAND);
        given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .body(containsString(
                        String.format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"travels\",version=\"%s\"} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)));
    }

    private void whenNewTravel(Traveller traveller, Trip trip) {
        Model m = travelsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("traveller", traveller);
        parameters.put("trip", trip);

        m.fromMap(parameters);

        this.processInstance = travelsProcess.createInstance(m);
        this.processInstance.start();
    }

    private void whenConfirmTravel() {
        WorkItem workItem = thenNextStepIs(STEP_CONFIRM_TRAVEL, SECURITY_POLICY_JDOE);
        Map<String, Object> results = new HashMap<>();
        results.put("approve", true);
        processInstance.completeWorkItem(workItem.getId(), results, SECURITY_POLICY_JDOE);
    }

    private void whenAddVisaApplication() {
        Map<String, Object> results = new HashMap<>();
        results.put("visaApplication", new VisaApplication("Jan", "Kowalski", "New York", "US", 10, "XXX098765"));
        results.put("visaResolution", new VisaResolution(true, "Test reason"));
        WorkItem workItem = thenNextStepIs(STEP_VISA_APPLICATION, SECURITY_POLICY_JDOE);
        processInstance.completeWorkItem(workItem.getId(), results, SECURITY_POLICY_JDOE);
    }

    private WorkItem thenNextStepIs(String expected, SecurityPolicy policy) {
        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());
        WorkItem next = workItems.get(0);
        assertEquals(expected, next.getName());
        return next;
    }

    private void thenProcessIsActive() {
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, this.processInstance.status());
    }

    private void thenProcessIsCompleted() {
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, this.processInstance.status());
    }

    private void thenHotelAndFlightAreChosen() {
        Model result = (Model) processInstance.variables();
        assertEquals(6, result.toMap().size());
        Hotel hotel = (Hotel) result.toMap().get("hotel");
        assertNotNull(hotel);
        assertEquals("Perfect hotel", hotel.getName());
        assertEquals("XX-012345", hotel.getBookingNumber());
        assertEquals("09876543", hotel.getPhone());

        Flight flight = (Flight) result.toMap().get("flight");
        assertNotNull(flight);
        assertEquals("MX555", flight.getFlightNumber());
        assertNotNull(flight.getArrival());
        assertNotNull(flight.getDeparture());
    }
}
