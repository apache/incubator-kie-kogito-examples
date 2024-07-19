/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.examples;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/publish")
@Produces(MediaType.APPLICATION_JSON)
public class PublishRestService {

    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(PublishRestService.class);

    @PostConstruct
    void init() {
        objectMapper = new ObjectMapper();
    }

    @Path("/{type}/{number}")
    @POST
    public Response publishEvenNumber(@PathParam("type") String type, @PathParam("number") int number) {
        logger.info("Publish type " + type + " number " + number);
        // check if the input number is even
        if (!"even".equals(type)) {
            return Response.status(Status.BAD_REQUEST).entity(objectMapper.createObjectNode().put("error", "Perfect square assessment not supported for odd numbers by this service")).build();
        }
        return Response.ok().entity(objectMapper.createObjectNode().put("perfect", isPerfectSquare(number))).build();
    }

    private boolean isPerfectSquare(int number) {
        double sqrt = Math.sqrt(number);
        return (sqrt == Math.round(sqrt));
    }

}
