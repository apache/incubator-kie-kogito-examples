/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.examples;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.Application;
import org.kie.kogito.examples.hr.DepartmentModel;
import org.kie.kogito.rules.KieRuntimeBuilder;

@Path("/department")
public class DepartmentEndpoint {

    @Inject
    KieRuntimeBuilder runtimeBuilder;
    
    @Inject
    Application app;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DepartmentModel assignDepartment(DepartmentModel resource) {
        KieSession ksession = runtimeBuilder.newKieSession("defaultStatelessKieSession", app.config().rule());

        ksession.insert( resource );
        int i = ksession.fireAllRules();

        return resource;
    }
}
