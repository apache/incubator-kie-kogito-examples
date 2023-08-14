package org.acme.serverless.loanbroker.aggregator;

import java.io.InputStream;
import java.io.OutputStream;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;

@Singleton
public class CloudEventDataFormat implements DataFormat {

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        if (graph instanceof CloudEvent) {
            final byte[] serialized = EventFormatProvider.getInstance()
                    .resolveFormat(JsonFormat.CONTENT_TYPE)
                    .serialize((CloudEvent) graph);
            stream.write(serialized);
        } else if (graph != null) {
            throw new IllegalArgumentException("Object " + graph + " is not a CloudEvent instance");
        }
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        return null;
    }

}
