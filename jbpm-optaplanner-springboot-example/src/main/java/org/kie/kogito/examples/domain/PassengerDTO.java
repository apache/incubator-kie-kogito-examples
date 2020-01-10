package org.kie.kogito.examples.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class PassengerDTO {

    private String name;
    // Sometimes null
    private String seatTypePreference;

    // At least 15 years old, sufficient mobility, strength and dexterity to assist in an evacuation.
    private boolean emergencyExitRowCapable;

    private boolean payedForSeat;

    public PassengerDTO() {
    }

    public PassengerDTO(String name, String seatTypePreference, boolean emergencyExitRowCapable, boolean payedForSeat) {
        this.name = name;
        this.seatTypePreference = seatTypePreference;
        this.emergencyExitRowCapable = emergencyExitRowCapable;
        this.payedForSeat = payedForSeat;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeatTypePreference() {
        return seatTypePreference;
    }

    public void setSeatTypePreference(String seatTypePreference) {
        this.seatTypePreference = seatTypePreference;
    }

    public boolean isEmergencyExitRowCapable() {
        return emergencyExitRowCapable;
    }

    public void setEmergencyExitRowCapable(boolean emergencyExitRowCapable) {
        this.emergencyExitRowCapable = emergencyExitRowCapable;
    }

    public boolean isPayedForSeat() {
        return payedForSeat;
    }

    public void setPayedForSeat(boolean payedForSeat) {
        this.payedForSeat = payedForSeat;
    }

}
