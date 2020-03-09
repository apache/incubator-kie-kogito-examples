package org.kie.kogito.examples.domain;

public class PassengerDTO {

    private String name;
    // Sometimes null
    private String seatTypePreference;

    // At least 15 years old, sufficient mobility, strength and dexterity to assist in an evacuation.
    private boolean emergencyExitRowCapable;

    private boolean paidForSeat;
    // not-null iff payedForSeat is true
    private String seat;

    public PassengerDTO() {
    }

    public PassengerDTO(String name, String seatTypePreference, boolean emergencyExitRowCapable, boolean paidForSeat, String seat) {
        this.name = name;
        this.seatTypePreference = seatTypePreference;
        this.emergencyExitRowCapable = emergencyExitRowCapable;
        this.paidForSeat = paidForSeat;
        this.seat = seat;
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

    public boolean hasPaidForSeat() {
        return paidForSeat;
    }

    public void setPaidForSeat(boolean paidForSeat) {
        this.paidForSeat = paidForSeat;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
