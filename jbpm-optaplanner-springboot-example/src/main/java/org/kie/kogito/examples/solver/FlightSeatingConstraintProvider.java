package org.kie.kogito.examples.solver;

import org.kie.kogito.examples.domain.FlightInfo;
import org.kie.kogito.examples.domain.Passenger;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

import static org.optaplanner.core.api.score.stream.Joiners.equal;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Function;
import java.util.function.Supplier;

public class FlightSeatingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                seatConflict(factory),
                emergencyExitRow(factory),
                seatTypePreference(factory),
                //planeBalance(factory)
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

    /*private Constraint planeBalance(ConstraintFactory factory) {
        return factory.from(Passenger.class).join(FlightInfo.class).groupBy(new BiConstraintCollector<Passenger, FlightInfo, CenterOfGravity, Pair<Pair<BigDecimal, BigDecimal>, Integer>>() {
                @Override
                public Supplier<CenterOfGravity> supplier() {
                    return CenterOfGravity::new;
                }

                @Override
                public TriFunction<CenterOfGravity, Passenger, FlightInfo, Runnable> accumulator() {
                    return (container, passenger, flightInfo) -> {
                        container.setFlightInfo(flightInfo);
                        container.addPassenger(passenger);
                        return () -> {
                            container.removePassenger(passenger);
                        };
                    };
                }

                @Override
                public Function<CenterOfGravity, Pair<Pair<BigDecimal, BigDecimal>, Integer>> finisher() {
                    return CenterOfGravity::get;
                }
            }
        ).penalize("Plane Balance", HardSoftScore.ONE_SOFT, cog -> {
            return (int) Math.round(Point2D.distance(0, 0, cog.getLeft().getLeft().doubleValue(), cog.getLeft().getRight().doubleValue()) * cog.getRight() * 100);
        });
    }*/

    /*
    private static final class CenterOfGravity {
        private FlightInfo flightInfo;
        private Pair<Pair<BigDecimal, BigDecimal>, Integer> cog;
        
        public CenterOfGravity() {
            cog = Pair.of(Pair.of(BigDecimal.ZERO, BigDecimal.ZERO), 0);
        }
        
        public Pair<Pair<BigDecimal, BigDecimal>, Integer> get() {
            if (cog.getRight().equals(0)) {
                return cog;
            }
            BigDecimal averageX = cog.getLeft().getLeft().divide(BigDecimal.valueOf(cog.getRight()), MathContext.DECIMAL128);
            BigDecimal averageY = cog.getLeft().getRight().divide(BigDecimal.valueOf(cog.getRight()), MathContext.DECIMAL128);
            return Pair.of(Pair.of(averageX, averageY), cog.getRight());
        }

        public void setFlightInfo(FlightInfo flightInfo) {
            if (this.flightInfo != null) {
                this.flightInfo = flightInfo;
            }
            else if (this.flightInfo != flightInfo) {
                throw new IllegalStateException("There should only be one FlightInfo");
            }
        }
        
        public void addPassenger(Passenger passenger) {
            Pair<Pair<BigDecimal, BigDecimal>, Integer> passengerCog = getPassengerCoG(passenger);
            
            Pair<BigDecimal,BigDecimal> cogPoint1 = cog.getLeft();
            Pair<BigDecimal,BigDecimal> cogPoint2 = passengerCog.getLeft();
            Integer cogWeight1 = cog.getRight();
            Integer cogWeight2 = passengerCog.getRight();
            Integer combinedWeight = cogWeight1 + cogWeight2;

            BigDecimal newX = cogPoint1.getLeft().add(cogPoint2.getLeft());
            BigDecimal newY = cogPoint1.getRight().add(cogPoint2.getRight());
            
            cog = Pair.of(Pair.of(newX, newY), combinedWeight);
        }

        public void removePassenger(Passenger passenger) {
            Pair<Pair<BigDecimal, BigDecimal>, Integer> passengerCog = getPassengerCoG(passenger);
            
            Pair<BigDecimal,BigDecimal> cogPoint1 = cog.getLeft();
            Pair<BigDecimal,BigDecimal> cogPoint2 = passengerCog.getLeft();
            Integer cogWeight1 = cog.getRight();
            Integer cogWeight2 = passengerCog.getRight();
            Integer subtractedWeight = cogWeight1 - cogWeight2;

            BigDecimal newX = cogPoint1.getLeft().subtract(cogPoint2.getLeft());
            BigDecimal newY = cogPoint1.getRight().subtract(cogPoint2.getRight());
    
            cog = Pair.of(Pair.of(newX, newY), subtractedWeight);
        }

        private Pair<Pair<BigDecimal, BigDecimal>, Integer> getPassengerCoG(Passenger passenger) {
            return Pair.of(
                Pair.of(BigDecimal.valueOf(passenger.getSeat().getColumn() - flightInfo.getSeatColumnSize() / 2),
                BigDecimal.valueOf(passenger.getSeat().getRow() - flightInfo.getSeatRowSize() / 2d)),
                1);
        }

    }*/

}
