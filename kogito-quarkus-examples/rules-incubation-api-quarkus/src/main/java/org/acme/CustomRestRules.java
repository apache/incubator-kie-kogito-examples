/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme;

import java.util.Map;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.kie.kogito.examples.Hello;
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
