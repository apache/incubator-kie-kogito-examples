/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.examples.domain;

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
