package org.acme.serverless.loanbroker.aggregator.resources;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.serverless.loanbroker.aggregator.QuotesRepositoryProcessor;
import org.acme.serverless.loanbroker.aggregator.model.BankQuote;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST interface to provide the quotes
 */
@Path("/quotes")
@ApplicationScoped
public class QuotesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotesResource.class);

    @Inject
    QuotesRepositoryProcessor quotesRepository;

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "200", description = "success", content = {
            @Content(schema = @Schema(implementation = BankQuote.class, type = SchemaType.ARRAY))
    })
    public Response fetchQuotesByInstance(@PathParam("id") final String instanceId) {
        final List<BankQuote> quotes = quotesRepository.fetchQuotesByInstanceId(instanceId);
        if (quotes == null || quotes.isEmpty()) {
            LOGGER.info("Empty quotes for instance {}. Returning NOT FOUND", instanceId);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        LOGGER.info("Returning {} quotes for workflow instance id {}", quotes.size(), instanceId);
        return Response.ok(quotes).build();
    }

}
