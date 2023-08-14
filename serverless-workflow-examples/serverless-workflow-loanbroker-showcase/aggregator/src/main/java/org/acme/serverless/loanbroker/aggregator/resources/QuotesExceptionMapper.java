package org.acme.serverless.loanbroker.aggregator.resources;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class QuotesExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        return Response.serverError().entity(ResponseError.SERVER_ERROR.withCause(exception)).build();
    }
}
