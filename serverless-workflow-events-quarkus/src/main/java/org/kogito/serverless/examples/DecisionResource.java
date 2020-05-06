package org.kogito.serverless.examples;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/decisions")
public class DecisionResource {

    @Inject
    @Channel("out-decisions")
    Publisher<JsonNode> decisions;

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType("application/json")
    public Publisher<JsonNode> streamDecisions() {
        return decisions;
    }
}
