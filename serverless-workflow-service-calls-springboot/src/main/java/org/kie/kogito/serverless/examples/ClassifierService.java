package org.kie.kogito.serverless.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

@Component
@ApplicationScope
public class ClassifierService {
    private static final Logger LOG = Logger.getLogger(ClassifierService.class);

    private Set<Country> classifiedCountries = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

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

    public Set<Country> getClassifiedCountries() {
        return classifiedCountries;
    }

}
