package org.drools.simple.project;

import org.kie.submarine.rules.DataSource;
import org.kie.submarine.rules.Unit;
import org.kie.submarine.rules.impl.ListDataSource;
import org.submarine.quickstart.Person;
import org.submarine.quickstart.Result;

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
