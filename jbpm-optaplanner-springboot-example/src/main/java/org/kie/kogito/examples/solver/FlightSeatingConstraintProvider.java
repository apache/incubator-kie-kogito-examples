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
import java.util.Set;
import java.util.HashSet;

public class FlightSeatingConstraintProvider implements ConstraintProvider {

    private Set<MassPoint> massPointSet = new HashSet<>();
    int times = 0;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
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
        return factory.from(Passenger.class).join(FlightInfo.class).groupBy(new BiConstraintCollector<Passenger, FlightInfo, CenterOfGravity, MassPoint>() {
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
                public Function<CenterOfGravity, MassPoint> finisher() {
                    return CenterOfGravity::get;
                }
            }
        ).penalize("Plane Balance", HardSoftScore.ONE_SOFT, cog -> {
            massPointSet.add(cog);
            if (times == 1000) {
                System.out.printf("Size: %d\n", massPointSet.size());
                massPointSet.forEach(mp -> System.out.println(cog.toString()));
            }
            times++;
            return (int) Math.round(Point2D.distance(0, 0, cog.getX().doubleValue(), cog.getY().doubleValue()) * cog.getMass() * 100);
        });
    }

    private static final class MassPoint {
        private final BigDecimal x;
        private final BigDecimal y;
        private final int mass;
        private static MassPoint ZERO = new MassPoint(BigDecimal.ZERO, BigDecimal.ZERO, 0);

        public MassPoint(BigDecimal x, BigDecimal y, int mass) {
            this.x = x;
            this.y = y;
            this.mass = mass;
        }

        public MassPoint add(MassPoint other) {
            return new MassPoint(x.add(other.x), y.add(other.y), mass + other.mass);
        }

        public MassPoint subtract(MassPoint other) {
            return new MassPoint(x.subtract(other.x), y.subtract(other.y), mass - other.mass);
        }

        public BigDecimal getX() {
            return x;
        }

        public BigDecimal getY() {
            return y;
        }

        public int getMass() {
            return mass;
        }

        @Override
        public String toString() {
            return String.format("%dkg at (%s, %s)", mass, x.toPlainString(), y.toPlainString());
        }

        public boolean equals(Object o) {
            if (o instanceof MassPoint) {
                MassPoint mp = (MassPoint) o;
                return x.equals(mp.x) && y.equals(mp.y) && mass == mp.mass;
            }
            return false;
        }

        public int hashCode() {
            return (31 * (31 * x.hashCode()) ^ y.hashCode()) ^ Integer.hashCode(mass);
        }

    }

    private static final class CenterOfGravity {
        private FlightInfo flightInfo;
        private MassPoint cog;
        
        public CenterOfGravity() {
            cog = MassPoint.ZERO;
        }
        
        public MassPoint get() {
            if (cog.getMass() == 0) {
                return cog;
            }
            BigDecimal averageX = cog.getX().divide(BigDecimal.valueOf(cog.getMass()), MathContext.DECIMAL128);
            BigDecimal averageY = cog.getY().divide(BigDecimal.valueOf(cog.getMass()), MathContext.DECIMAL128);
            return new MassPoint(averageX, averageY, cog.getMass());
        }

        public void setFlightInfo(FlightInfo flightInfo) {
            this.flightInfo = flightInfo;
        }
        
        public void addPassenger(Passenger passenger) {
            MassPoint passengerCog = getPassengerCoG(passenger); 
            cog = cog.add(passengerCog);
        }

        public void removePassenger(Passenger passenger) {
            MassPoint passengerCog = getPassengerCoG(passenger);
            cog = cog.subtract(passengerCog);
        }

        private MassPoint getPassengerCoG(Passenger passenger) {
            return new MassPoint(
                BigDecimal.valueOf(passenger.getSeat().getColumn() - flightInfo.getSeatColumnSize() / 2),
                BigDecimal.valueOf(passenger.getSeat().getRow() - flightInfo.getSeatRowSize() / 2d),
                1);
        }

    }

}
