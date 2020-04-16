package org.kie.kogito.examples.domain;

import java.util.Objects;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class Seat {

    @PlanningId
    private String name;
    private int row;
    private int column;
    private SeatType seatType;
    private boolean emergencyExitRow;

    public Seat() {
    }

    public Seat(int row, int column, SeatType seatType, boolean emergencyExitRow) {
        this.row = row;
        this.column = column;
        // ASCII has a nice property: The English Alphabet are placed in consecutive
        // ASCII codes. So 'B' is immediately after 'A', 'C' is immediately after 'B',
        // etc. So 'A' + n = nth letter of the alphabet.
        // Name is row number (starting at 1) + column letter (starting at 'A').
        this.name = (row + 1) + Character.toString((char) ('A' + column));
        this.seatType = seatType;
        this.emergencyExitRow = emergencyExitRow;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public boolean isEmergencyExitRow() {
        return emergencyExitRow;
    }
}
