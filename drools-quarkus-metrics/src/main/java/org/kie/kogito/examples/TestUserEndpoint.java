package org.kie.kogito.examples;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/test")
public class TestUserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUserEndpoint.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllKeys() {
        LOGGER.info("test");
        return "test";
    }
}
