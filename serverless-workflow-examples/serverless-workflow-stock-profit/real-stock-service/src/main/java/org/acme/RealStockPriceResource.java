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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;

@Path("/stock-price")
public class RealStockPriceResource {

    public final SecureRandom SECURE_RANDOM = new SecureRandom();

    @GET
    @Path("/{symbol}")
    @Produces(MediaType.APPLICATION_JSON)
    public Stock get(@PathParam("symbol") String symbol) {
        // Emulates a stock variation
        float randomFloat = SECURE_RANDOM.nextFloat();
        BigDecimal price = BigDecimal.valueOf(randomFloat).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        return new Stock(symbol, price);
    }
}