package org.acme.serverless.loanbroker.aggregator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
