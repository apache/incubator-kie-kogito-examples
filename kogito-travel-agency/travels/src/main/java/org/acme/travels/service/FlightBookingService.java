package org.acme.travels.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.travels.Flight;
import org.acme.travels.Trip;

@ApplicationScoped
public class FlightBookingService {

    private Boolean shouldTimeout = Boolean.FALSE;

    public void setShouldTimeout(Boolean shouldTimeout) {
        this.shouldTimeout = shouldTimeout;
    }

    public Boolean getShouldTimeout() {
        return shouldTimeout;
    }

    public Flight bookFlight(Trip trip) {
        if (shouldTimeout) {
            throw new RuntimeException("Failed to connect to Flight Booking system: connection timeout");
        } else {
            return new Flight("MX555", trip.getBegin(), trip.getEnd());
        }
    }
}
