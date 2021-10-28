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
package org.acme.messaging;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.User;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusStraightThroughProcessService;
import org.kie.kogito.incubation.application.AppRoot;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.processes.ProcessIds;

@ApplicationScoped
public class HelloConsumer {

    @Inject
    QuarkusStraightThroughProcessService processService;

    @Inject
    AppRoot appRoot;

    /**
     * React to messages from the incoming channel starting the process and publishing the response to the outgoing channel.
     * 
     * @param user payload
     * @return the response
     */
    @Incoming("hello")
    @Outgoing("hello-response-publisher")
    public DataContext onMessage(User user) {
        MapDataContext context = MapDataContext.of(Collections.singletonMap("user", user));
        return processService.evaluate(appRoot.get(ProcessIds.class).get("hello"), context);
    }
}
