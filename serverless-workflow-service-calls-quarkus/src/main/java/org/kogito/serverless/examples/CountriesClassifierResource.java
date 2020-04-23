package org.kogito.serverless.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/countryclassifier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountriesClassifierResource {

    private static final Logger LOG = Logger.getLogger(CountriesClassifierResource.class);

    private Set<Country> classifiedCountries = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    @GET
    public Set<Country> list() {
        return classifiedCountries;
    }


    public JsonNode classifySmallMedium(JsonNode classifiedCountryNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Country classifiedCountry = mapper.treeToValue(classifiedCountryNode, Country.class);
            classifiedCountry.setClassifier("Small/Medium");
            classifiedCountries.add(classifiedCountry);
            JsonNode retNode = mapper.convertValue(classifiedCountry, JsonNode.class);

            return retNode;
        } catch(Exception e) {
            LOG.error("unable to classify country: " + classifiedCountryNode.toString());
            return classifiedCountryNode;
        }
    }

    public JsonNode classifyLarge(JsonNode classifiedCountryNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Country classifiedCountry = mapper.treeToValue(classifiedCountryNode, Country.class);
            classifiedCountry.setClassifier("Large");
            classifiedCountries.add(classifiedCountry);

            JsonNode retNode = mapper.convertValue(classifiedCountry, JsonNode.class);

            return retNode;
        } catch(Exception e) {
            LOG.error("unable to classify country: " + classifiedCountryNode.toString());
            return classifiedCountryNode;
        }
    }
}
