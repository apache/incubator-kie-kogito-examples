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
package org.acme.serverless.loanbroker.aggregator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.serverless.loanbroker.aggregator.model.BankQuote;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saves every aggregated exchange in memory for later retrieval.
 */
@ApplicationScoped
public class InMemoryQuotesRepositoryProcessor implements QuotesRepositoryProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryQuotesRepositoryProcessor.class);

    private final Map<String, List<BankQuote>> quotesMap = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        if (exchange != null) {
            final String instanceId = (String) exchange.getIn().getHeader(IntegrationConstants.KOGITO_FLOW_ID_HEADER);

            if (instanceId != null && !instanceId.isEmpty()) {
                final Integer quoteCount = (Integer) exchange.getIn()
                        .getHeader(QuotesAggregationStrategy.HEADER_QUOTES_COUNT);

                quotesMap.computeIfAbsent(instanceId, k -> {
                    final List<BankQuote> quotes = (List<BankQuote>) exchange.getIn().getBody();
                    if (quotes == null || quotes.isEmpty()) {
                        return null;
                    }
                    return quotes;
                });
                LOGGER.info("Aggregation for workflow instance {} ended with {} quotes", instanceId, quoteCount);
            } else {
                throw new IllegalStateException(
                        String.format("Received an exchange with empty instance id. '%s' header not present",
                                IntegrationConstants.KOGITO_FLOW_ID_HEADER));
            }

        }

    }

    @Override
    public List<BankQuote> fetchQuotesByInstanceId(String instanceId) {
        if (instanceId == null) {
            return Collections.emptyList();
        }
        LOGGER.info("Fetching quotes for workflow instance {}", instanceId);
        return quotesMap.getOrDefault(instanceId, Collections.emptyList());
    }

    @Override
    public void clear() {
        quotesMap.clear();
    }

}
