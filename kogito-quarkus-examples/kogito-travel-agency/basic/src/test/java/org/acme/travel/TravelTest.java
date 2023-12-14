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
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class TravelTest {

    @Inject
    @Named("travels")
    Process<? extends Model> travelsProcess;

    @Test
    public void testTravelNoVisaRequired() {

        assertNotNull(travelsProcess);

        Model m = travelsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("traveller", new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US")));
        parameters.put("trip", new Trip("New York", "US", new Date(), new Date()));

        m.fromMap(parameters);

        ProcessInstance<?> processInstance = travelsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        Model result = (Model) processInstance.variables();
        assertEquals(4, result.toMap().size());
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

        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("ConfirmTravel", workItems.get(0).getName());

        processInstance.completeWorkItem(workItems.get(0).getId(), null);

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testTravelVisaRequired() {

        assertNotNull(travelsProcess);

        Model m = travelsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        Traveller requested = new Traveller("Jan", "Kowalski", "jan.kowalski@example.com", "Polish", new Address("polna", "Krakow", "32000", "Poland"));
        parameters.put("traveller", requested);
        parameters.put("trip", new Trip("New York", "US", new Date(), new Date()));

        m.fromMap(parameters);

        ProcessInstance<?> processInstance = travelsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("VisaApplication", workItems.get(0).getName());

        String visaApplication = "http://mydrive.example.com/docs/u324dx";
        Map<String, Object> params = Collections.singletonMap("visaApplication", visaApplication);
        processInstance.completeWorkItem(workItems.get(0).getId(), params);

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        Model result = (Model) processInstance.variables();
        assertEquals(4, result.toMap().size());

        Traveller traveller = (Traveller) result.toMap().get("traveller");
        assertNotNull(traveller);
        assertEquals(requested.getFirstName(), traveller.getFirstName());
        assertEquals(requested.getLastName(), traveller.getLastName());
        assertEquals(requested.getEmail(), traveller.getEmail());
        assertEquals(requested.getNationality(), traveller.getNationality());
        assertEquals(requested.getAddress().getCity(), traveller.getAddress().getCity());
        assertEquals(requested.getAddress().getCountry(), traveller.getAddress().getCountry());
        assertEquals(requested.getAddress().getStreet(), traveller.getAddress().getStreet());
        assertEquals(requested.getAddress().getZipCode(), traveller.getAddress().getZipCode());
        assertEquals(visaApplication, traveller.getVisaApplication());

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

        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("ConfirmTravel", workItems.get(0).getName());

        processInstance.completeWorkItem(workItems.get(0).getId(), null);

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }
}
