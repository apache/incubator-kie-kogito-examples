package org.kie.kogito.examples;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.examples.domain.Flight;
import org.kie.kogito.examples.domain.Passenger;
import org.kie.kogito.examples.domain.Seat;
import org.kie.kogito.examples.domain.SeatType;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FlightSeatingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class FlightSeatingTest {

    @Inject
    @Named("flightProcess")
    Process<? extends Model> process;


    @Test
    public void runProcess() {
        assertNotNull(process);

        Model m = process.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flight", generateFlight());
        

        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = process.createInstance(m);
        processInstance.start();
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());
        
        Model result = (Model)processInstance.variables();
        assertEquals(1, result.toMap().size());
        Flight flight = (Flight) result.toMap().get("flight");

        assertNotNull(flight);

        Map<Seat, List<Passenger>> seatToPassengerMap = flight.getPassengerList().stream()
                .collect(Collectors.groupingBy(Passenger::getSeat, Collectors.toList()));
        for (Seat seat : flight.getSeatList()) {
            List<Passenger> passengerList = seatToPassengerMap.get(seat);
            System.out.println(seat.getName() + ": " + (passengerList == null ? "empty"
                    : passengerList.stream().map(Passenger::getName).collect(Collectors.joining(", "))));
        }
    }


    private static Flight generateFlight() {
        Flight flight = new Flight();
        flight.setOrigin("BRU");
        flight.setDestination("SFO");
        LocalDate departureDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(2);
        flight.setDepartureDateTime(LocalDateTime.of(departureDate, LocalTime.of(9, 0)));
        int seatRowSize = 5;
        int seatColumnSize = 6;
        flight.setSeatRowSize(seatRowSize);
        flight.setSeatColumnSize(seatColumnSize);
        int leftMiddleColumn = (seatColumnSize - 1) / 2;
        List<Seat> seatList = new ArrayList<>(seatRowSize * seatColumnSize);
        for (int row = 0; row < seatRowSize; row++) {
            for (int column = 0; column < seatColumnSize; column++) {
                SeatType seatType = (column == 0 || column == seatColumnSize - 1) ? SeatType.WINDOW
                        : (column == leftMiddleColumn || column == leftMiddleColumn + 1) ? SeatType.AISLE
                        : SeatType.OTHER;
                boolean emergencyExitRow = (row == (seatRowSize / 2));
                Seat seat = new Seat(row, column, seatType, emergencyExitRow);
                seatList.add(seat);
            }
        }
        flight.setSeatList(seatList);
        int passengerSize = seatList.size() * 3 / 4;
        Random random = new Random(37);
        List<Passenger> passengerList = new ArrayList<>(passengerSize);
        for (int i = 0; i < passengerSize; i++) {
            String name = "Passenger " + (i + 1);
            SeatType seatTypePreference = (random.nextDouble() < 0.40) ? SeatType.WINDOW
                    : (random.nextDouble() < 0.20) ? SeatType.AISLE
                    : null;
            boolean emergencyExitRowCapable = (random.nextDouble() < 0.90);
            passengerList.add(new Passenger(name, seatTypePreference, emergencyExitRowCapable));
        }
        flight.setPassengerList(passengerList);
        return flight;
    }

}
