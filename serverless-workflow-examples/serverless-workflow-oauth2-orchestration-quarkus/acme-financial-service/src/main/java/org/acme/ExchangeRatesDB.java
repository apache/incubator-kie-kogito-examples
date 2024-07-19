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

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.annotation.PostConstruct;

/**
 * Emulates the Acme Financial Services database, implementation and maintenance of this DB
 * is out of the scope of this example.
 */
@ApplicationScoped
public class ExchangeRatesDB {

    private static final Map<String, Double> EURO_TO_OTHERS_EXCHANGE_RATES = new HashMap<>();

    @PostConstruct
    void initialize() {
        // Information based on European Central Bank for the 10 th June 2022...
        EURO_TO_OTHERS_EXCHANGE_RATES.put("EUR", 1.0);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("USD", 1.0578);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("JPY", 141.69);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("BGN", 1.9558);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("CZK", 24.705);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("DKK", 7.4389);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("GBP", 0.85048);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("HUF", 398.48);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("PLN", 4.6053);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("RON", 4.9442);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("SEK", 10.5255);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("CHF", 1.0404);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("ISK", 137.70);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("NOK", 10.1495);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("HRK", 7.5225);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("TRY", 18.0116);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("AUD", 1.4845);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("BRL", 5.1718);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("CAD", 1.3484);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("CNY", 7.0868);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("HKD", 8.3031);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("IDR", 15393.27);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("ILS", 3.5626);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("INR", 82.3355);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("KRW", 1344.25);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("MXN", 20.8285);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("MYR", 4.6564);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("NZD", 1.6482);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("PHP", 56.101);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("SGD", 1.4620);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("THB", 36.774);
        EURO_TO_OTHERS_EXCHANGE_RATES.put("ZAR", 16.5209);
    }

    /**
     * @return never null.
     */
    public Double readExchangeRate(String currencyFrom, String currencyTo, String exchangeDate) {
        Double euroToCurrencyFrom = EURO_TO_OTHERS_EXCHANGE_RATES.get(currencyFrom);
        if (euroToCurrencyFrom == null) {
            return 1d;
        }
        Double euroToCurrencyTo = EURO_TO_OTHERS_EXCHANGE_RATES.get(currencyTo);
        if (euroToCurrencyTo == null) {
            return 1d;
        }
        return (1 / euroToCurrencyFrom) * euroToCurrencyTo;
    }
}
