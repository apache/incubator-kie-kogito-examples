package org.kie.kogito.examples.solver;

import org.kie.kogito.examples.domain.Guest;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;

public class WeddingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {factory.from(Guest.class)
                .groupBy(Guest::getTable, count())
                .filter((table, guestCount) -> table.getCapacity() < guestCount)
                .penalize("Table capacity", HardSoftScore.ONE_HARD,
                        (table, guestCount) -> guestCount - table.getCapacity())};
    }

}
