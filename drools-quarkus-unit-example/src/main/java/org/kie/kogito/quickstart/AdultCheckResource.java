package org.kie.kogito.quickstart;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/persons")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdultCheckResource {

    @Inject
    AdultCheckService service;

    @POST
    public Response post(Person p) {
        service.post(p);
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/adults")
    public List<Person> adults() {
        return service.adults();
    }

    @GET
    @Path("/all")
    public List<Person> all() {
        return service.persons();
    }
}