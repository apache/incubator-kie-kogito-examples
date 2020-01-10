package org.kie.kogito.examples.service;

import org.kie.kogito.examples.domain.Flight;
import org.kie.kogito.examples.domain.FlightDTO;
import org.kie.kogito.examples.domain.Passenger;
import org.kie.kogito.examples.domain.PassengerDTO;
import org.kie.kogito.examples.domain.Seat;
import org.kie.kogito.examples.domain.SeatType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Component
public class FlightService {

	public Flight createFlight(FlightDTO flightDTO) throws IllegalArgumentException {
        final Flight newFlight = new Flight();
        if (flightDTO.getSeatColumnSize() % 2 != 0) {
            throw new IllegalArgumentException("There must be an even number of seats in each row.");
        }
        else if (flightDTO.getSeatColumnSize() < 4) {
            throw new IllegalArgumentException("There must be at least four seats in each row.");
        }
        else if (flightDTO.getSeatRowSize() < 1) {
            throw new IllegalArgumentException("There must be at least one row.");
        }
        try {
            newFlight.setOrigin(flightDTO.getOrigin());
            newFlight.setDestination(flightDTO.getDestination());
            newFlight.setDepartureDateTime(LocalDateTime.parse(flightDTO.getDepartureDateTime()));
            newFlight.setSeatRowSize(flightDTO.getSeatRowSize());
            newFlight.setSeatColumnSize(flightDTO.getSeatColumnSize());
            List<Seat> seatList = new ArrayList<Seat>(flightDTO.getSeatRowSize() * flightDTO.getSeatColumnSize());
            for (int row = 0; row < flightDTO.getSeatRowSize(); row++) {
                final int MIDDLE_OF_ROW = flightDTO.getSeatColumnSize() / 2;
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
        }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid Date", e);
        }
    }

    public Flight addPassengerToFlight(Flight flight, PassengerDTO passenger) {
        flight.getPassengerList().add(new Passenger(passenger.getName(), SeatType.valueOf(passenger.getSeatTypePreference()), passenger.isEmergencyExitRowCapable()));
        return flight;
    }
}
