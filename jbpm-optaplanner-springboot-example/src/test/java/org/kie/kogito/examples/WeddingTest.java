/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.examples.domain.Guest;
import org.kie.kogito.examples.domain.Table;
import org.kie.kogito.examples.domain.WeddingSolution;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class WeddingTest {

    @Inject
    @Named("wedding")
    Process<? extends Model> process;


    @Test
    public void testWedding() {
        assertNotNull(process);

        Model m = process.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("weddingPlan", generateWeddingPlan());
        

        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = process.createInstance(m);
        processInstance.start();
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());
        
        Model result = (Model)processInstance.variables();
        assertEquals(1, result.toMap().size());
        WeddingSolution weddingSolution = (WeddingSolution) result.toMap().get("weddingPlan");

        assertNotNull(weddingSolution);

        weddingSolution.getGuestList().stream().collect(Collectors.groupingBy(Guest::getTable, Collectors.toList()))
                .forEach((table, guests) -> {
                    System.out.println("Table: " + guests.stream().map(Guest::getName).collect(Collectors.joining(", ")));
                });
    }


    private static WeddingSolution generateWeddingPlan() {
        WeddingSolution problem = new WeddingSolution();
        problem.setGuestList(IntStream.range(0, 120).mapToObj(i -> {
            Guest guest = new Guest();
            guest.setName("Guest " + i);
            return guest;
        }).collect(Collectors.toList()));
        problem.setTableList(IntStream.range(0, 12).mapToObj(i -> {
            Table table = new Table();
            table.setCapacity(10);
            return table;
        }).collect(Collectors.toList()));
        return problem;
    }

}
