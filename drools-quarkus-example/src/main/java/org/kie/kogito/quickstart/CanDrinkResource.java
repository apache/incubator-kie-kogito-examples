package org.kie.kogito.quickstart;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.drools.modelcompiler.SessionMemory;
import org.kie.kogito.rules.RuleUnit;

@Path("/candrink/{name}/{age}")
public class CanDrinkResource {

    @Named("canDrinkKS")
    RuleUnit<SessionMemory> ruleUnit;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String canDrink( @PathParam("name") String name, @PathParam("age") int age ) {
        SessionMemory memory = new SessionMemory();

        Result result = new Result();
        memory.add(result);
        memory.add(new Person( name, age ));

        ruleUnit.evaluate(memory);

        return result.toString();
    }
}