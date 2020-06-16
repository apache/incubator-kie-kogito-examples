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
package org.kie.kogito.examples.solver;

import org.kie.kogito.examples.domain.FlightInfo;
import org.kie.kogito.examples.domain.Passenger;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import static org.optaplanner.core.api.score.stream.Joiners.equal;

import java.awt.geom.Point2D;

public class FlightSeatingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                seatConflict(factory),
                emergencyExitRow(factory),
                seatTypePreference(factory),
                planeBalance(factory)
        };
    }

    private Constraint seatConflict(ConstraintFactory factory) {
        return factory
                .fromUniquePair(Passenger.class, equal(Passenger::getSeat))
                .penalize("Seat conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint emergencyExitRow(ConstraintFactory factory) {
        return factory
                .from(Passenger.class)
                .filter(passenger -> passenger.getSeat().isEmergencyExitRow()
                        && !passenger.isEmergencyExitRowCapable())
                .penalize("Emergency exit row has incapable passenger", HardSoftScore.ONE_HARD);
    }

    private Constraint seatTypePreference(ConstraintFactory factory) {
        return factory
                .from(Passenger.class)
                .filter(passenger -> passenger.getSeat().getSeatType().violatesPreference(
                        passenger.getSeatTypePreference()))
                .penalize("Seat type preference", HardSoftScore.ONE_SOFT);
    }

    private Constraint planeBalance(ConstraintFactory factory) {
        return factory.from(Passenger.class).groupBy(ConstraintCollectors.sum(p -> p.getSeat().getColumn()))
                .join(factory.from(Passenger.class).groupBy(ConstraintCollectors.sum(p -> p.getSeat().getRow())))
                .join(factory.from(Passenger.class).groupBy(ConstraintCollectors.count()))
                .join(FlightInfo.class)
                .penalize("Plane Balance", HardSoftScore.ONE_SOFT, (totalX, totalY, passengerCount, flightInfo) -> {
                    double localTotalX = totalX - passengerCount * ((flightInfo.getSeatColumnSize() / 2.0) - 0.5);
                    double localTotalY = totalY - passengerCount * ((flightInfo.getSeatRowSize() / 2.0) - 0.5);
                    double averageX = localTotalX / passengerCount;
                    double averageY = localTotalY / passengerCount;
                    return (int) Math.round(Point2D.distance(0, 0, averageX, averageY) * passengerCount * 100);
                });
    }
}
