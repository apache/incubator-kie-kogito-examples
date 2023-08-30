/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.examples;

import java.util.Map;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.incubation.application.AppRoot;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.rules.RuleUnitIds;
import org.kie.kogito.incubation.rules.services.RuleUnitService;

@Path("/custom-rest-rules")
public class CustomRestRules {

    @Inject
    AppRoot appRoot;
    @Inject
    RuleUnitService svc;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Stream<String> helloUnit(Map<String, Object> payload) {
        // path: /rule-units/org.kie.kogito.examples.Hello/queries/hello

        var queryId = appRoot.get(RuleUnitIds.class)
                .get(Hello.class)
                .queries()
                .get("hello");
        DataContext ctx = MapDataContext.of(payload);
        return svc.evaluate(queryId, ctx) // Stream<DataContext>
                .map(dc -> dc.as(MapDataContext.class).get("$s", String.class));
    }

}
