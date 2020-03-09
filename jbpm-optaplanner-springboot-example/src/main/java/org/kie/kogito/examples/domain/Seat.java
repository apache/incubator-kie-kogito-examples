package org.kie.kogito.examples.domain;

import java.util.Objects;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class Seat {

    @PlanningId
    private final String name;
    private final int row;
    private final int column;
    private final SeatType seatType;
    private final boolean emergencyExitRow;

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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Seat) {
            Seat other = (Seat) o;
            // Name is computed based on row and column and thus does not need to be checked.
            // (seatType is based on column and emergencyExitRow is based on row, but not guaranteed
            // by API (we need to know row and column length to calculate this here))
            return this.row == other.row &&
                    this.column == other.column &&
                    this.seatType.equals(other.getSeatType()) &&
                    this.emergencyExitRow == other.emergencyExitRow;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Name is computed based on row and column and thus does not need to be included in the hash.
        return Objects.hash(row, column, seatType, emergencyExitRow);
    }
}
