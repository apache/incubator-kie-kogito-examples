package org.acme.travels.service;

import javax.enterprise.context.ApplicationScoped;

import org.acme.travels.Address;
import org.acme.travels.Hotel;
import org.acme.travels.Trip;

@ApplicationScoped
public class HotelBookingService {

	public Hotel bookHotel(Trip trip) {
		return new Hotel("Perfect hotel", new Address("street", trip.getCity(), "12345", trip.getCountry()), "09876543", "XX-012345");
	}
}
