/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme.serverless.loanbroker.aggregator;

import java.net.URI;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

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
