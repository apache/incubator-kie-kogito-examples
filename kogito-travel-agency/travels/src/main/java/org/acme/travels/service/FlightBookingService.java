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
package org.acme.travels.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.travels.Flight;
import org.acme.travels.Trip;

@ApplicationScoped
public class FlightBookingService {

    private Boolean shouldTimeout = Boolean.FALSE;

    public void setShouldTimeout(Boolean shouldTimeout) {
        this.shouldTimeout = shouldTimeout;
    }

    public Boolean getShouldTimeout() {
        return shouldTimeout;
    }

    public Flight bookFlight(Trip trip) {
        if (shouldTimeout) {
            throw new RuntimeException("Failed to connect to Flight Booking system: connection timeout");
        } else {
            return new Flight("MX555", trip.getBegin(), trip.getEnd());
        }
    }
}
