package org.acme.travel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.acme.travels.Address;
import org.acme.travels.Flight;
import org.acme.travels.Traveller;
import org.acme.travels.Trip;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import io.quarkus.test.junit.QuarkusTest;

@Disabled("Disabled by default as it requires inifinispan and kafka")
@QuarkusTest
public class BookFlightTest {

	@Inject
	@Named("flightBooking")
	Process<? extends Model> bookFlightProcess;
	
	@Test
	public void testBookingFlight() {
		
		assertNotNull(bookFlightProcess);
		
		Model m = bookFlightProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("traveller", new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US")));
        parameters.put("trip", new Trip("New York", "US", new Date(), new Date()));

        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = bookFlightProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status()); 
        
        Model result = (Model)processInstance.variables();
        assertEquals(3, result.toMap().size());
        Flight flight = (Flight) result.toMap().get("flight");
        assertNotNull(flight);
        assertEquals("MX555", flight.getFlightNumber());
        assertNotNull(flight.getArrival());
        assertNotNull(flight.getDeparture());
	}
}
