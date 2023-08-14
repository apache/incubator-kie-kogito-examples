/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.identity.SecurityIdentity;

@Path("financial-service")
public class AcmeFinancialResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcmeFinancialResource.class);

    @Inject
    SecurityIdentity identity;

    @Inject
    ExchangeRatesDB exchangeRatesDB;

    @GET
    @Path("exchange-rate")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "exchangeRate")
    @SecurityRequirement(name = "acme-financial-oauth")
    public ExchangeRateResult getExchangeRate(@QueryParam("currencyFrom") String currencyFrom,
            @QueryParam("currencyTo") String currencyTo,
            @QueryParam("exchangeDate") String exchangeDate) {
        LOGGER.debug("getExchangeRate, accessedBy: {}, currencyFrom: {}, currencyTo: {}, exchangeDate: {}",
                identity.getPrincipal().getName(), currencyFrom, currencyTo, exchangeDate);
        Double exchangeRate = exchangeRatesDB.readExchangeRate(currencyFrom, currencyTo, exchangeDate);
        return new ExchangeRateResult(exchangeRate);
    }
}
