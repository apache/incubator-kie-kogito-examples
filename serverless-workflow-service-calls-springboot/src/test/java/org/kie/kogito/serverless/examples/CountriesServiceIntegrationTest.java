package org.kie.kogito.serverless.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.serverless.examples.CountriesService;
import org.kie.kogito.serverless.examples.ServerlessServiceCallsExampleApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ServerlessServiceCallsExampleApplication.class)
@RunWith(SpringRunner.class)
public class CountriesServiceIntegrationTest {

    @Autowired
    CountriesService countriesService;

    @Test
    public void testJavaServiceCall() throws Exception {

        assertNotNull(countriesService);

        String sampleDataStr = "{\"name\": \"Greece\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode sampleData = mapper.readTree(sampleDataStr);

        JsonNode results = countriesService.getCountryInfo(sampleData);
        assertNotNull(results);
        assertEquals("Greece", results.get("name").asText());
        assertEquals("Europe", results.get("region").asText());
    }
}
