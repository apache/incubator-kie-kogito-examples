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
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.rules.units.SessionData;
import org.kie.kogito.rules.units.SessionUnit;

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