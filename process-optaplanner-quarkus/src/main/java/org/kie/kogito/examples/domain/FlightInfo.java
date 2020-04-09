package org.kie.kogito.examples.domain;

import java.time.LocalDateTime;

public class FlightInfo {

    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private int seatRowSize;
    private int seatColumnSize;

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

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
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