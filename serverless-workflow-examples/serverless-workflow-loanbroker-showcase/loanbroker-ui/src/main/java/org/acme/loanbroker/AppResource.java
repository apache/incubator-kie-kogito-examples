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
package org.acme.loanbroker;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.loanbroker.domain.QuotesResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.mutiny.core.eventbus.EventBus;

@Path("/")
@ApplicationScoped
public class AppResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppResource.class);

    @Inject
    QuotesRepository quotesRepository;

    @Inject
    EventBus bus;

    @ConfigProperty(name = "org.acme.loanbroker.ui.workflowURL")
    String workflowURL;

    @CheckedTemplate
    static class Templates {
        static native TemplateInstance app(String workflowURL);
    }

    @Path("app.js")
    @GET
    @Produces("text/javascript")
    public TemplateInstance appJS() {
        return Templates.app(workflowURL);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeQuotesEvent(CloudEvent cloudEvent) {
        LOGGER.info("Received Cloud Event {}", cloudEvent);
        if (cloudEvent == null || cloudEvent.getData() == null) {
            return Response.status(400).entity("{ \"message\": \"CloudEvent without data\" }").build();
        }
        final QuotesResponse quotes = this.quotesRepository.add(cloudEvent);
        bus.publish("new-quote", quotes);
        return Response.ok().build();
    }

    @GET()
    @Path("/quotes")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, QuotesResponse> listQuotes() {
        return this.quotesRepository.list();
    }

}
