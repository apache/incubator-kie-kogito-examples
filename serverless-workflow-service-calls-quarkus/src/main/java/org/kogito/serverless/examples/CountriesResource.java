package org.kogito.serverless.examples;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;


@Path("/country")
public class CountriesResource {

    @Inject
    @RestClient
    CountriesService countriesService;


    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Country name(@PathParam("name") String name) {
        return countriesService.getByName(name).iterator().next();
    }

    public JsonNode jsonName(JsonNode nameNode) {
        Country country = countriesService.getByName(nameNode.get("name").asText()).iterator().next();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode retNode = mapper.convertValue(country, JsonNode.class);

        return retNode;
    }
}
