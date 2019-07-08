package org.acme.travels.service;

import javax.enterprise.context.ApplicationScoped;

import org.acme.travels.Flight;
import org.acme.travels.Trip;

@ApplicationScoped
public class FlightBookingService {

	public Flight bookFlight(Trip trip) {
		return new Flight("KLM009988", trip.getBegin(), trip.getEnd());
	}
}
