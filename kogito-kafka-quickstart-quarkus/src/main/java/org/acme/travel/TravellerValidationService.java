package org.acme.travel;

import org.kie.kogito.rules.*;

public class TravellerValidationService implements RuleUnitData {
    private final SingletonStore<Traveller> traveller = DataSource.createSingleton();

    public SingletonStore<Traveller> getTraveller() {
        return traveller;
    }
}
