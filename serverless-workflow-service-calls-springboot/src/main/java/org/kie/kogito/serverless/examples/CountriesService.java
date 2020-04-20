package org.kie.kogito.serverless.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CountriesService {

    private static final Logger LOG = Logger.getLogger(CountriesService.class);

    @Autowired
    private RestTemplate restTemplate;

    public JsonNode getCountryInfo(JsonNode nameNode) {
        JsonNode retNode = null;

        try {

            ResponseEntity<Country[]> response =
                    restTemplate.getForEntity(
                            "https://restcountries.eu/rest/v2/name/{name}",
                            Country[].class, nameNode.get("name").asText());
            Country[] countries = response.getBody();
            Country country = countries[0]; // we just get the first for this example

            ObjectMapper mapper = new ObjectMapper();
            retNode = mapper.convertValue(country, JsonNode.class);

        } catch (RestClientException e) {
            LOG.error(e.getMessage());
        }

        return retNode;
    }
}
