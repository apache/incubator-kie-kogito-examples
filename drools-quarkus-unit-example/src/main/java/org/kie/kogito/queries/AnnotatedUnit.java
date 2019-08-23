package org.kie.kogito.queries;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitMemory;
import org.kie.kogito.rules.annotations.When;

public class AnnotatedUnit implements RuleUnitMemory {
    private final DataStore<Person> persons = DataSource.createStore();

    public void adultRule(
            @When("/persons[ age >= 18 ]") Person adult,
            @When("/persons[ age != 1 ]") Person anotherBinding) {

        System.out.printf("%s is an adult\n", adult.getName());
    }

    public void notAdultRule(
            @When("/persons[ age < 18 ]") Person adult) {

        System.out.printf("%s is _not_ an adult\n", adult.getName());
    }


    public DataStore<Person> getPersons() {
        return persons;
    }

    public AnnotatedUnit getUnit() { return this; }
}
