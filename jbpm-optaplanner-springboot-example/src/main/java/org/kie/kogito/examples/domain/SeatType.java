package org.kie.kogito.examples.domain;

public enum SeatType {
    WINDOW,
    AISLE,
    OTHER;

    public boolean violatesPreference(SeatType seatTypePreference) {
        if (seatTypePreference == null) {
            return false;
        }
        return this != seatTypePreference;
    }

}
