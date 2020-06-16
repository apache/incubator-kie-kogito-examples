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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.examples.domain.FlightDTO;
import org.kie.kogito.examples.domain.PassengerDTO;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FlightSeatingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// reset spring context after each test method
public class FlightTest {

    @Autowired
    Processes processMaker;

    @Test
    public void runProcess() {
        Process<? extends Model> process = processMaker.processById("flights");
        assertNotNull(process);

        FlightDTO flightParams = new FlightDTO();
        flightParams.setDepartureDateTime(LocalDateTime.now().toString());
        flightParams.setDestination("");
        flightParams.setOrigin("");
        flightParams.setSeatColumnSize(10);
        flightParams.setSeatRowSize(6);

        Model m = process.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("params", flightParams);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = process.createInstance(m);
        processInstance.start();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());
        // add new passenger
        processInstance.send(Sig.of("newPassengerRequest", new PassengerDTO("john", "NONE", false, false, null)));

        List<WorkItem> tasks = processInstance.workItems();
        assertEquals(2, tasks.size());
        // approve passenger
        Map<String, Object> result = new HashMap<>();
        result.put("isPassengerApproved", true);
        processInstance.completeWorkItem(tasks.get(1).getId(), result);
        // close the passenger list so no more passengers can be added
        result = new HashMap<>();
        result.put("isPassengerListFinalized", true);
        processInstance.completeWorkItem(tasks.get(0).getId(), result);

        try {
            Thread.sleep(10000);
        } catch (Exception e) {

        }

        tasks = processInstance.workItems();

        assertEquals(1, tasks.size());
        // then complete the flight
        processInstance.completeWorkItem(tasks.get(0).getId(), null);
        // flight process is done
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());

        Model resultData = (Model) processInstance.variables();
        assertEquals(5, resultData.toMap().size());
    }
}
