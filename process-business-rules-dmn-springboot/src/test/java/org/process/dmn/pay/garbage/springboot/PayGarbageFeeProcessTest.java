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
package org.process.dmn.pay.garbage.springboot;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.tests.KogitoInfinispanSpringbootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoInfinispanSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class PayGarbageFeeProcessTest {

    @Autowired
    @Qualifier("org.process.dmn.pay.garbage.springboot.paygarbagefee")
    Process<? extends Model> payGarbageFeeProcess;

    @Test
    public void testTemporalResidenceInLondonForTwo() {

        Model m = payGarbageFeeProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("peopleCount", 2);
        parameters.put("residence", new Residence("London", "temporal"));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = payGarbageFeeProcess.createInstance(m);
        processInstance.start();

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());
        Model result = (Model) processInstance.variables();
        Assertions.assertTrue(result.toMap().containsKey("fee"), "The result should be stored in the fee variable");
        Assertions.assertEquals(BigDecimal.valueOf(1325), ((GarbageFee) (result.toMap().get("fee"))).getAmount());
    }
}
