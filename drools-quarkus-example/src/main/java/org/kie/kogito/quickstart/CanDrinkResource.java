package org.kie.kogito.quickstart;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.rules.units.impl.SessionData;
import org.kie.kogito.rules.units.impl.SessionUnit;

@Path("/candrink/{name}/{age}")
public class CanDrinkResource {

    @Inject @Named("canDrinkKS")
    SessionUnit ruleUnit;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String canDrink( @PathParam("name") String name, @PathParam("age") int age ) {
        SessionData data = new SessionData();

        Result result = new Result();
        data.add(result);
        data.add(new Person( name, age ));

        ruleUnit.evaluate(data);

        return result.toString();
    }
}