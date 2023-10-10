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
package org.acme.numbers;

import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/numbers")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class NumbersResource {

    private Random rand;

    @PostConstruct
    void init() {
        rand = new Random();
    }

    @GET
    @Path("random")
    public Response getRandom(@QueryParam("bound") @DefaultValue("10") int bound) {
        return fromNumber(rand.nextInt(bound));
    }

    @POST
    @Path("{number}/multiplyByAndSum")
    public Response multiplyByAndSum(@PathParam("number") int multiplier, Numbers numbers) {
        return fromNumber(numbers.getNumbers().stream().map(n -> n.intValue() * multiplier).collect(Collectors.summingInt(Integer::intValue)));
    }

    private Response fromNumber(int number) {
        return Response.ok().entity(number).build();
    }

}
