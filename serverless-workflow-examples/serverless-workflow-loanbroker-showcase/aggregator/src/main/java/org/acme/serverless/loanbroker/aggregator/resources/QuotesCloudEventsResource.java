package org.acme.serverless.loanbroker.aggregator.resources;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.serverless.loanbroker.aggregator.IntegrationConstants;
import org.acme.serverless.loanbroker.aggregator.model.BankQuote;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

@Path("/")
@ApplicationScoped
public class QuotesCloudEventsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotesCloudEventsResource.class);

    /**
     * Produced by Camel
     */
    @Produce("direct:aggregator")
    ProducerTemplate aggregatorProducer;

    @Inject
    ObjectMapper mapper;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeQuoteEvent(CloudEvent cloudEvent) {
        LOGGER.info("Aggregator just received an event \n {}", cloudEvent);
        if (cloudEvent == null || cloudEvent.getData() == null) {
            LOGGER.warn("Bad Event Received, no data. Ignoring. See: \n {}", cloudEvent);
            return Response.status(400).entity(ResponseError.NO_DATA_EVENT_ERROR).build();
        }
        if (cloudEvent.getExtension(IntegrationConstants.KOGITO_FLOW_ID_HEADER) == null) {
            LOGGER.warn("Bad Event Received, no Kogito header. Ignoring. See: \n {}", cloudEvent);
            return Response.status(400).entity(ResponseError.NO_DATA_EVENT_ERROR).build();
        }
        aggregatorProducer.sendBodyAndHeader(
                "direct:aggregator",
                PojoCloudEventDataMapper.from(mapper, BankQuote.class).map(cloudEvent.getData()).getValue(),
                IntegrationConstants.KOGITO_FLOW_ID_HEADER,
                cloudEvent.getExtension(IntegrationConstants.KOGITO_FLOW_ID_HEADER).toString());
        return Response.ok().build();
    }

}
