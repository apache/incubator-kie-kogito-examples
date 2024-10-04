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
package org.acme.serverless.loanbroker.aggregator.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
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
