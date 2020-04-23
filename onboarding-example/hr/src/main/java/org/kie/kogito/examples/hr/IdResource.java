package org.kie.kogito.examples.hr;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/id")
public class IdResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdModel hello(IdModel id) {
        id.setEmployeeId( String.valueOf((id.getEmployee().getFirstName() + id.getEmployee().getLastName()).hashCode()));
        return id;
    }
}