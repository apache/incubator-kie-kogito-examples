/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.submarine.quarkus.example;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.submarine.rest.quarkus.DMNModelInfo;
import org.kie.dmn.submarine.rest.quarkus.DMNModelInfoList;
import org.kie.dmn.submarine.rest.quarkus.DMNResult;
import org.kie.dmn.submarine.rest.quarkus.DMNSubmarineQuarkus;

@Path("/")
public class GenericDMNEndpoint {

    static final DMNRuntime dmnRuntime = DMNSubmarineQuarkus.createGenericDMNRuntime();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DMNResult dmn(@HeaderParam("X-DMN-model-namespace") String modelNamespace,
                         @HeaderParam("X-DMN-model-name") String modelName,
                         Map<String, Object> dmnContext) {
        if (dmnRuntime.getModels().size() > 1 && (modelNamespace == null || modelName == null)) {
            throw new MultipleModelsMissingParams();
        }
        return DMNSubmarineQuarkus.evaluate(dmnRuntime,
                                            modelNamespace,
                                            modelName,
                                            dmnContext);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DMNModelInfoList dmn() {
        List<DMNModel> models = dmnRuntime.getModels();
        List<DMNModelInfo> result = models.stream().map(DMNModelInfo::of).collect(Collectors.toList());
        return new DMNModelInfoList(result);
    }

    public static class MultipleModelsMissingParams extends RuntimeException {

        public MultipleModelsMissingParams() {
            super("There are multiple DMN models. MUST provide X-DMN-model-namespace, X-DMN-model-name header parameters, to indentify DMN model to be evaluated.");
        }
    }

    @Provider
    public static class MultipleModelsMissingParamsMapper implements ExceptionMapper<MultipleModelsMissingParams> {

        public Response toResponse(MultipleModelsMissingParams e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
