package org.kie.dmn.submarine.quarkus.example;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.submarine.rest.quarkus.DMNModelInfo;
import org.kie.dmn.submarine.rest.quarkus.DMNResult;
import org.kie.dmn.submarine.rest.quarkus.DMNSubmarineQuarkus;

@Path("/dmn/{modelName}")
public class ByNameDMNEndpoint {

    static final DMNRuntime dmnRuntime = DMNSubmarineQuarkus.createGenericDMNRuntime();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DMNResult dmn(@PathParam("modelName") String modelName, Map<String, Object> dmnContext) {
        return DMNSubmarineQuarkus.evaluate(dmnRuntime,
                                            modelName,
                                            dmnContext);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DMNModelInfo dmn(@PathParam("modelName") String modelName) {
        return DMNModelInfo.of(DMNSubmarineQuarkus.modelByName(dmnRuntime, modelName));
    }

}
