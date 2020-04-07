package org.acme.travel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.acme.travels.Address;
import org.acme.travels.Flight;
import org.acme.travels.Hotel;
import org.acme.travels.Traveller;
import org.acme.travels.Trip;
import org.acme.travels.VisaApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;

import io.quarkus.test.junit.QuarkusTest;

@Disabled("Disabled by default as it requires inifinispan and kafka")
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
        
        Model result = (Model)processInstance.variables();
        assertEquals(5, result.toMap().size());
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
        parameters.put("traveller", new Traveller("Jan", "Kowalski", "jan.kowalski@example.com", "Polish", new Address("polna", "Krakow", "32000", "Poland")));
        parameters.put("trip", new Trip("New York", "US", new Date(), new Date()));

        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = travelsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());                
        assertEquals("VisaApplication", workItems.get(0).getName());
        
        Map<String, Object> results = new HashMap<>();
        results.put("visaApplication", new VisaApplication("Jan", "Kowalski", "New York", "US", 10, "XXX098765"));
        
        processInstance.completeWorkItem(workItems.get(0).getId(), results);
        
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());
        
        Model result = (Model)processInstance.variables();
        assertEquals(5, result.toMap().size());
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
