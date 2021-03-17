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
package org.kie.kogito.examples.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.examples.domain.Flight;
import org.kie.kogito.examples.domain.FlightDTO;
import org.kie.kogito.examples.domain.FlightInfo;
import org.kie.kogito.examples.domain.Passenger;
import org.kie.kogito.examples.domain.PassengerDTO;
import org.kie.kogito.examples.domain.Seat;
import org.kie.kogito.examples.domain.SeatType;

@ApplicationScoped
public class FlightService {

    public Flight createFlight(FlightDTO flightDTO) throws IllegalArgumentException {
        final Flight newFlight = new Flight();
        if (flightDTO.getSeatColumnSize() % 2 != 0) {
            throw new IllegalArgumentException("There must be an even number of seats in each row.");
        } else if (flightDTO.getSeatColumnSize() < 4) {
            throw new IllegalArgumentException("There must be at least four seats in each row.");
        } else if (flightDTO.getSeatRowSize() < 1) {
            throw new IllegalArgumentException("There must be at least one row.");
        }
        try {
            FlightInfo flightInfo = new FlightInfo();
            flightInfo.setOrigin(flightDTO.getOrigin());
            flightInfo.setDestination(flightDTO.getDestination());
            flightInfo.setDepartureDateTime(LocalDateTime.parse(flightDTO.getDepartureDateTime()));
            flightInfo.setSeatRowSize(flightDTO.getSeatRowSize());
            flightInfo.setSeatColumnSize(flightDTO.getSeatColumnSize());
            newFlight.setFlightInfo(flightInfo);
            List<Seat> seatList = new ArrayList<Seat>(flightDTO.getSeatRowSize() * flightDTO.getSeatColumnSize());
            final int MIDDLE_OF_ROW = flightDTO.getSeatColumnSize() / 2;
            for (int row = 0; row < flightDTO.getSeatRowSize(); row++) {
                final boolean IS_EMERGENCY_EXIT_ROW = row == (flightDTO.getSeatRowSize() / 2);
                seatList.add(new Seat(row, 0, SeatType.WINDOW, IS_EMERGENCY_EXIT_ROW));
                seatList.add(new Seat(row, flightDTO.getSeatColumnSize() - 1, SeatType.WINDOW, IS_EMERGENCY_EXIT_ROW));
                seatList.add(new Seat(row, MIDDLE_OF_ROW - 1, SeatType.AISLE, IS_EMERGENCY_EXIT_ROW));
                seatList.add(new Seat(row, MIDDLE_OF_ROW, SeatType.AISLE, IS_EMERGENCY_EXIT_ROW));

                for (int column = 1; column < MIDDLE_OF_ROW - 1; column++) {
                    seatList.add(new Seat(row, column, SeatType.OTHER, IS_EMERGENCY_EXIT_ROW));
                    seatList.add(new Seat(row, MIDDLE_OF_ROW + column, SeatType.OTHER, IS_EMERGENCY_EXIT_ROW));
                }
            }
            newFlight.setSeatList(seatList);
            newFlight.setPassengerList(new ArrayList<>(flightDTO.getSeatRowSize() * flightDTO.getSeatColumnSize()));
            return newFlight;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid Date", e);
        }
    }

    public Flight addPassengerToFlight(Flight flight, PassengerDTO passenger) {
        Seat seat = null;
        if (passenger.hasPaidForSeat()) {
            String[] seatLoc = passenger.getSeat().split(";");
            int row = Integer.parseInt(seatLoc[0]);
            int col = Integer.parseInt(seatLoc[1]);
            seat = flight.getSeatList().stream().filter(s -> s.getRow() == row && s.getColumn() == col).findAny().get();
        }
        flight.getPassengerList().add(new Passenger(Long.valueOf(flight.getPassengerList().size()), passenger.getName(),
                (passenger.getSeatTypePreference().equals("NONE")) ? null : SeatType.valueOf(passenger.getSeatTypePreference()),
                passenger.isEmergencyExitRowCapable(), passenger.hasPaidForSeat(), seat));
        return flight;
    }
}
