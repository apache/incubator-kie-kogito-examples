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
package org.acme.travel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import javax.inject.Inject;

import org.acme.travels.Address;
import org.acme.travels.Traveller;
import org.acme.travels.Trip;
import org.drools.core.common.InternalAgenda;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.KieRuntimeBuilder;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class VisaRulesIT {

	@Inject
	KieRuntimeBuilder ruleRuntime;
	
	@Test
	public void testVisaNotRequiredRule() {
		
		assertNotNull(ruleRuntime);
		
		Traveller traveller = new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US"));
        Trip trip = new Trip("New York", "US", new Date(), new Date());

        KieSession ksession = ruleRuntime.newKieSession();
        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup("visas");
        ksession.insert(trip);
        ksession.insert(traveller);
        ksession.fireAllRules();
        
        ksession.dispose();
        
        assertFalse(trip.isVisaRequired());                
	}
	
	@Test
	public void testVisaRequiredRule() {
		
		assertNotNull(ruleRuntime);
		
		Traveller traveller = new Traveller("Jan", "Kowalski", "jan.kowalski@example.com", "Polish", new Address("polna", "Krakow", "32000", "Poland"));
        Trip trip = new Trip("New York", "US", new Date(), new Date());

        KieSession ksession = ruleRuntime.newKieSession();
        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup("visas");
        ksession.insert(trip);
        ksession.insert(traveller);
        ksession.fireAllRules();
        
        ksession.dispose();
        
        assertTrue(trip.isVisaRequired());                
	}
}
