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
package org.kie.kogito.examples.sw.temp.subtraction;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationResource {

    @POST
    @APIResponseSchema(value = OperationResource.Result.class, responseDescription = "SubtractionResult", responseCode = "200")
    public Response doOperation(@NotNull SubtractionOperation operation) {
        return Response.ok(new Result(operation.getLeftElement() - operation.getRightElement())).build();
    }

    @RegisterForReflection
    public static final class Result {

        float difference;

        public Result() {
        }

        public Result(final float difference) {
            this.difference = difference;
        }

        public float getDifference() {
            return difference;
        }
    }
}
