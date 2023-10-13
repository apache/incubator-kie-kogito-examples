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
package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Map;

@Path("/profit")
public class ProfitResource {

    private final Map<String, BigDecimal> portfolio = Map.of(
            "XPTO", BigDecimal.valueOf(50.25),
            "ABCD", BigDecimal.valueOf(35.80),
            "KGTO", BigDecimal.valueOf(50),
            "KIE", BigDecimal.valueOf(76.89)
    );

    static BigDecimal calculateProfit(BigDecimal currentPrice, BigDecimal previousPrice) {
        return currentPrice.subtract(previousPrice).divide(previousPrice, 2, RoundingMode.HALF_UP);
    }

    @Path("/{symbol}")
    @GET
    public StockProfit getProfit(@PathParam("symbol") String symbol, @QueryParam("currentPrice") BigDecimal currentPrice) {
        BigDecimal portfolioPrice = portfolio.get(symbol.toUpperCase());
        if (portfolioPrice != null) {
            BigDecimal profit = calculateProfit(currentPrice, portfolioPrice);
            return new StockProfit(symbol, NumberFormat.getPercentInstance().format(profit));
        } else {
            throw new IllegalArgumentException("Unknown stock symbol: " + symbol);
        }
    }
}
