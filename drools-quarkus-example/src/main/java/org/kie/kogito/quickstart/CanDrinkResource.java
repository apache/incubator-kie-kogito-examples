package org.kie.kogito.quickstart;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.drools.modelcompiler.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

@Path("/candrink/{name}/{age}")
public class CanDrinkResource {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String canDrink( @PathParam("name") String name, @PathParam("age") int age ) {
        KieSession ksession = runtimeBuilder.newKieSession("canDrinkKS");

        Result result = new Result();
        ksession.insert(result);
        ksession.insert(new Person( name, age ));
        ksession.fireAllRules();

        return result.toString();
    }
}