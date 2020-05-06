package org.kogito.serverless.examples;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.json.Json;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Random;

@Path("/newapplicant")
public class ApplicantResource {

    Random rand = new Random();

    @Inject
    @Channel("out-applicants")
    Emitter<String> newApplicantEmitter;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitApplicant(JsonNode newApplicant) {
        CloudEventImpl<JsonNode> applicantEvent =
                CloudEventBuilder.<JsonNode>builder()
                        .withId(String.valueOf(rand.nextInt(1000)))
                        .withType("newApplicantEvent")
                        .withSource(URI.create("http://localhost:8080"))
                        .withData(newApplicant)
                        .build();

        newApplicantEmitter.send(Json.encode(applicantEvent));

    }
}
