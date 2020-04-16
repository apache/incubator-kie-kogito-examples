/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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