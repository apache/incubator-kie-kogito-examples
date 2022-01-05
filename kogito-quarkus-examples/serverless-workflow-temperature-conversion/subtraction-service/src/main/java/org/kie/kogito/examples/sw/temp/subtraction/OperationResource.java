/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.examples.sw.temp.subtraction;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationResource {

    @POST
    public Response doOperation(@NotNull SubtractionOperation operation) {
        operation.setDifference(operation.getLeftElement() - operation.getRightElement());
        return Response.ok(new Result(operation)).build();
    }

    @RegisterForReflection
    public static final class Result {

        SubtractionOperation subtraction;

        public Result() {
        }

        public Result(final SubtractionOperation subtraction) {
            this.subtraction = subtraction;
        }

        public SubtractionOperation getSubtraction() {
            return subtraction;
        }

        public void setSubtraction(SubtractionOperation subtraction) {
            this.subtraction = subtraction;
        }
    }
}
