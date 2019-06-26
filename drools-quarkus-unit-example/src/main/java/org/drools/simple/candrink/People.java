package org.drools.simple.candrink;

import org.kie.kogito.quickstart.Person;
import org.kie.kogito.quickstart.Result;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.RuleUnitMemory;
import org.kie.kogito.rules.impl.ListDataSource;

public class People implements RuleUnitMemory {
    private final DataSource<Result> results = new ListDataSource<>();
    private final DataSource<Person> persons = new ListDataSource<>();

    public DataSource<Result> getResults() {
        return results;
    }

    public DataSource<Person> getPersons() {
        return persons;
    }
}
