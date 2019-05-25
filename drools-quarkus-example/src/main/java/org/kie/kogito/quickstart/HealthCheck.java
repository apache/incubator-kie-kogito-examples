package org.kie.kogito.quickstart;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/health")
public class HealthCheck {

    @GET
    public Response check() {
        return Response.ok().build();
    }
}
