package org.acme.travel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.acme.travels.Address;
import org.acme.travels.Hotel;
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
public class BookHotelTest {

	@Inject
	@Named("hotelBooking")
	Process<? extends Model> bookHotelProcess;
	
	@Test
	public void testBookingHotel() {
		
		assertNotNull(bookHotelProcess);
		
		Model m = bookHotelProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("traveller", new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US")));
        parameters.put("trip", new Trip("New York", "US", new Date(), new Date()));

        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = bookHotelProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status()); 
        
        Model result = (Model)processInstance.variables();
        assertEquals(3, result.toMap().size());
        Hotel hotel = (Hotel) result.toMap().get("hotel");
        assertNotNull(hotel);
        assertEquals("Perfect hotel", hotel.getName());
        assertEquals("XX-012345", hotel.getBookingNumber());
        assertEquals("09876543", hotel.getPhone());
	}
}
