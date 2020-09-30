/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.examples.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class Flight {
    
    @ProblemFactProperty
    private FlightInfo flightInfo = new FlightInfo();

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "seatRange")
    private List<Seat> seatList;

    @PlanningEntityCollectionProperty
    private List<Passenger> passengerList;

    @PlanningScore
    private HardSoftScore score;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public FlightInfo getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(FlightInfo flightInfo) {
        this.flightInfo = flightInfo;
    }

    public String getOrigin() {
        return flightInfo.getOrigin();
    }

    public void setOrigin(String origin) {
        flightInfo.setOrigin(origin);
    }

    public String getDestination() {
        return flightInfo.getDestination();
    }

    public void setDestination(String destination) {
        flightInfo.setDestination(destination);
    }

    public LocalDateTime getDepartureDateTime() {
        return flightInfo.getDepartureDateTime();
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        flightInfo.setDepartureDateTime(departureDateTime);
    }

    public int getSeatRowSize() {
        return flightInfo.getSeatRowSize();
    }

    public void setSeatRowSize(int seatRowSize) {
        flightInfo.setSeatRowSize(seatRowSize);
    }

    public int getSeatColumnSize() {
        return flightInfo.getSeatColumnSize();
    }

    public void setSeatColumnSize(int seatColumnSize) {
        flightInfo.setSeatColumnSize(seatColumnSize);
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

}
