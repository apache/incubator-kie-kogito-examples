package org.drools.simple.candrink;

import org.kie.kogito.quickstart.Person;
import org.kie.kogito.quickstart.Result;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.Unit;
import org.kie.kogito.rules.impl.ListDataSource;

@Unit
public class People {
    private final DataSource<Result> results = new ListDataSource<>();
    private final DataSource<Person> persons = new ListDataSource<>();

    public DataSource<Result> results() {
        return results;
    }

    public DataSource<Person> persons() {
        return persons;
    }
}
