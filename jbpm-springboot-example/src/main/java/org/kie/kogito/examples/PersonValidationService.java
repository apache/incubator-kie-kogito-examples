package org.kie.kogito.examples;

import org.kie.kogito.examples.demo.Person;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;

public class PersonValidationService implements RuleUnitData {
    private DataStore<Person> persons = DataSource.createStore();

    public DataStore<Person> getPersons() {
        return persons;
    }
}
