package org.kie.kogito.quickstart;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.KieRuntimeBuilder;

@Path("/candrink3/{name}/{age}")
public class CanDrinkWithDeclaredTypesResource {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String canDrink( @PathParam("name") String name, @PathParam("age") int age ) throws IllegalAccessException, InstantiationException {
        KieBase kbase = runtimeBuilder.getKieBase( "canDrinkWithTypesKB" );

        KieSession ksession = kbase.newKieSession();

        FactType resultType = kbase.getFactType( "org.drools.simple.declaredtypes", "DeclaredResult" );
        Object result = resultType.newInstance();

        FactType personType = kbase.getFactType( "org.drools.simple.declaredtypes", "DeclaredPerson" );
        Object person = personType.newInstance();
        personType.set( person, "name", name );
        personType.set( person, "age", age );

        ksession.insert(result);
        ksession.insert(person);

        ksession.fireAllRules();

        return result.toString();
    }
}