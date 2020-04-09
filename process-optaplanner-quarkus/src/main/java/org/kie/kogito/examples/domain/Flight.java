package org.kie.kogito.examples.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class Flight {

    @ProblemFactProperty
    private FlightInfo flightInfo = new FlightInfo();

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "seatRange")
    private List<Seat> seatList;

    @PlanningEntityCollectionProperty
    private List<Passenger> passengerList;

    @PlanningScore
    private HardSoftScore score;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public FlightInfo getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(FlightInfo flightInfo) {
        this.flightInfo = flightInfo;
    }

    public String getOrigin() {
        return flightInfo.getOrigin();
    }

    public void setOrigin(String origin) {
        flightInfo.setOrigin(origin);
    }

    public String getDestination() {
        return flightInfo.getDestination();
    }

    public void setDestination(String destination) {
        flightInfo.setDestination(destination);
    }

    public LocalDateTime getDepartureDateTime() {
        return flightInfo.getDepartureDateTime();
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        flightInfo.setDepartureDateTime(departureDateTime);
    }

    public int getSeatRowSize() {
        return flightInfo.getSeatRowSize();
    }

    public void setSeatRowSize(int seatRowSize) {
        flightInfo.setSeatRowSize(seatRowSize);
    }

    public int getSeatColumnSize() {
        return flightInfo.getSeatColumnSize();
    }

    public void setSeatColumnSize(int seatColumnSize) {
        flightInfo.setSeatColumnSize(seatColumnSize);
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
