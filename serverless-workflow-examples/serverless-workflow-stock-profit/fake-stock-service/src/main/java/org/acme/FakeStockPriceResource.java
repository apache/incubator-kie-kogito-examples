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
import java.math.BigDecimal;
import java.util.Map;

@Path("/stock-price")
public class FakeStockPriceResource {

    private final Map<String, BigDecimal> stocks = Map.of(
            "XPTO", BigDecimal.valueOf(10.99),
            "ABCD", BigDecimal.valueOf(30.50),
            "KGTO", BigDecimal.valueOf(75),
            "KIE", BigDecimal.valueOf(92.33)
    );

    @GET
    @Path("/{symbol}")
    public Stock get(@PathParam("symbol") String symbol) {
        BigDecimal price = stocks.get(symbol.toUpperCase());
        if (price != null) {
            return new Stock(symbol, price);
        } else {
            throw new IllegalArgumentException("Unknown stock symbol: " + symbol);
        }
    }
}