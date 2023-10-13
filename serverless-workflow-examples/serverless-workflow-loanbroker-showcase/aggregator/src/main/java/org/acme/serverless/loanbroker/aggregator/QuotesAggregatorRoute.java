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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.serverless.loanbroker.aggregator.model.AggregationResponse;
import org.apache.camel.Exchange;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.cloudevents.CloudEvent;

/**
 * Aggregation Strategy for all quotes received.
 * The payload must be a reference to the BankQuote model and the header must
 * include the {@link IntegrationConstants#KOGITO_FLOW_ID_HEADER}
 */
@ApplicationScoped
public class QuotesAggregatorRoute extends EndpointRouteBuilder {

    @Inject
    QuotesRepositoryProcessor quotesRepository;

    @Inject
    CloudEventsConverter cloudEventsConverter;

    @Inject
    CloudEventDataFormat cloudEventDataFormat;

    @ConfigProperty(name = "org.acme.serverless.loanbroker.aggregator.replyTo")
    String replyTo;

    @Override
    public void configure() {
                getContext()
                        .getTypeConverterRegistry()
                        .addTypeConverter(CloudEvent.class, AggregationResponse.class,
                        cloudEventsConverter);
                
                from("direct:aggregator")
                        .routeId("quotes-aggregator")
                        .aggregate(header(IntegrationConstants.KOGITO_FLOW_ID_HEADER), new QuotesAggregationStrategy())
                        .completionInterval(3000)
                        .process(quotesRepository)
                        .setBody(AggregationResponse::fromExchange)
                        .convertBodyTo(CloudEvent.class)
                        .marshal(cloudEventDataFormat)
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/cloudevents+json"))
                        .to(replyTo + "?copyHeaders=false");
    }

}
