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
package org.acme.workflow.financial.service;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("financial-service")
public class AcmeFinancialResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcmeFinancialResource.class);

    @Inject
    SecurityIdentity identity;

    @Inject
    StatementsDB statementsDB;

    @GET
    @Path("statement")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "getStatement")
    @RolesAllowed("customer")
    @SecurityRequirement(name = "acme-financial-oauth")
    public List<StatementEntry> getStatement() {
        LOGGER.info("Getting statement for user {}", identity.getPrincipal());
        return statementsDB.getStatementEntries(identity.getPrincipal().getName());
    }
}
