package org.kie.kogito.examples.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class Flight {

    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private int seatRowSize;
    private int seatColumnSize;

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
