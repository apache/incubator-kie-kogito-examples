package org.kie.kogito.examples;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.kie.addons.systemmonitoring.interceptor.PrometheusExceptionMapper;

@Provider
public class ActivateExceptionMapper extends PrometheusExceptionMapper {

    @Override
    public Response toResponse(Exception e) {
        return super.toResponse(e);
    }
}
