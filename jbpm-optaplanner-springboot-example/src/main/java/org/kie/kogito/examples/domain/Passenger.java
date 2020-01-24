package org.kie.kogito.examples.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Passenger {

    @PlanningId
    private Long id;

    private String name;
    // Sometimes null
    private SeatType seatTypePreference;
    // At least 15 years old, sufficient mobility, strength and dexterity to assist in an evacuation.
    private boolean emergencyExitRowCapable;
    @PlanningPin
    private boolean payedForSeat;

    @PlanningVariable(valueRangeProviderRefs = "seatRange")
    private Seat seat;

    public Passenger() {
    }

    public Passenger(Long id, String name, SeatType seatTypePreference, boolean emergencyExitRowCapable, boolean payedForSeat, Seat seat) {
        this.id = id;
        this.name = name;
        this.seatTypePreference = seatTypePreference;
        this.emergencyExitRowCapable = emergencyExitRowCapable;
        this.payedForSeat = payedForSeat;
        this.seat = seat;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SeatType getSeatTypePreference() {
        return seatTypePreference;
    }

    public boolean isEmergencyExitRowCapable() {
        return emergencyExitRowCapable;
    }

    public boolean isPayedForSeat() {
        return payedForSeat;
    }

    public Seat getSeat() {
        return seat;
    }

}
