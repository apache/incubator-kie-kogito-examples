package org.kie.kogito.examples.domain;

public class FlightDTO {

    private String origin;
    private String destination;
    private String departureDateTime;
    private int seatRowSize;
    private int seatColumnSize;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(String departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public int getSeatRowSize() {
        return seatRowSize;
    }

    public void setSeatRowSize(int seatRowSize) {
        this.seatRowSize = seatRowSize;
    }

    public int getSeatColumnSize() {
        return seatColumnSize;
    }

    public void setSeatColumnSize(int seatColumnSize) {
        this.seatColumnSize = seatColumnSize;
    }
}
