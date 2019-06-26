package org.kie.kogito.quickstart;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.simple.candrink.People;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

@Path("/candrink/{name}/{age}")
public class CanDrinkResource {

    @Inject
    RuleUnit<People> ruleUnit;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String canDrink( @PathParam("name") String name, @PathParam("age") int age ) {
        People people = new People();
        RuleUnitInstance<People> instance = ruleUnit.createInstance(people);

        Result result = new Result();
        Person person = new Person(name, age);

        people.getResults().add(result);
        people.getPersons().add(person);

        instance.fire();

        return result.toString();
    }
}