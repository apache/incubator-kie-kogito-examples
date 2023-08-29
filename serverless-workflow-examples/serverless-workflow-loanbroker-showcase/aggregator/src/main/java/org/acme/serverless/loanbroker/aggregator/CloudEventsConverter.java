package org.acme.serverless.loanbroker.aggregator;

import java.net.URI;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;

import static org.acme.serverless.loanbroker.aggregator.IntegrationConstants.KOGITO_FLOW_ID_HEADER;

@Singleton
public class CloudEventsConverter extends TypeConverterSupport {

    @Inject
    ObjectMapper mapper;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
        if (CloudEvent.class.equals(type)) {
            // In real-life use case, this can be a Any Object -> CloudEvents conversion.
            // One can keep the specific CE attributes in the Exchange header or attributes.
            final CloudEvent event = CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType("kogito.serverless.loanbroker.aggregated.quotes.response")
                    .withSource(URI.create("/kogito/serverless/loanbroker/aggregator"))
                    .withDataContentType(MediaType.APPLICATION_JSON)
                    .withData(PojoCloudEventData.wrap(value, mapper::writeValueAsBytes))
                    .withExtension(KOGITO_FLOW_ID_HEADER,
                            exchange.getIn().getHeader(KOGITO_FLOW_ID_HEADER).toString())
                    .build();
            return (T) event;
        }
        return null;
    }

}
