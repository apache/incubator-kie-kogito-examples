package org.acme.travels;

import java.util.Date;

public class Flight {

	private String flightNumber;
	private String seat;
	private String gate;
	private Date departure;
	private Date arrival;
	
	public Flight() {
		
	}

	public Flight(String flightNumber, Date departure, Date arrival) {
		super();
		this.flightNumber = flightNumber;
		this.departure = departure;
		this.arrival = arrival;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getGate() {
		return gate;
	}

	public void setGate(String gate) {
		this.gate = gate;
	}

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(Date departure) {
		this.departure = departure;
	}

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(Date arrival) {
		this.arrival = arrival;
	}

	@Override
	public String toString() {
		return "Flight [flightNumber=" + flightNumber + ", seat=" + seat + ", gate=" + gate + ", departure=" + departure
				+ ", arrival=" + arrival + "]";
	}
}
