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

public class FlightInfo {

    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private int seatRowSize;
    private int seatColumnSize;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public int getSeatRowSize() {
        return seatRowSize;
    }

    public void setSeatRowSize(int seatRowSize) {
        this.seatRowSize = seatRowSize;
    }

    public int getSeatColumnSize() {
        return seatColumnSize;
    }

    public void setSeatColumnSize(int seatColumnSize) {
        this.seatColumnSize = seatColumnSize;
    }
}