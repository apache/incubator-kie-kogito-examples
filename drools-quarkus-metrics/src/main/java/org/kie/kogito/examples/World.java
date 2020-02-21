package org.kie.kogito.examples;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;

public class World implements RuleUnitData {
    DataStore<String> strings = DataSource.createStore();

    public DataStore<String> getStrings() {
        return strings;
    }
}
