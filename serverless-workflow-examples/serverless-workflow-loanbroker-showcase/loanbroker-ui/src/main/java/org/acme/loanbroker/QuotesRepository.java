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
package org.acme.loanbroker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.acme.loanbroker.domain.QuotesResponse;

@ApplicationScoped
public class QuotesRepository {
    @Inject
    ObjectMapper mapper;

    private final Map<String, QuotesResponse> quotes = new ConcurrentHashMap<>();

    public QuotesResponse add(final CloudEvent cloudEvent) {
        final QuotesResponse quotesResponse = PojoCloudEventDataMapper.from(mapper, QuotesResponse.class).map(cloudEvent.getData()).getValue();
        final Object requestId = cloudEvent.getExtension("kogitoprocinstanceid");
        if (requestId != null) {
            quotesResponse.setEventType(cloudEvent.getType());
            quotesResponse.setLoanRequestId(requestId.toString());
            quotes.put(requestId.toString(), quotesResponse);
            return quotesResponse;
        }
        throw new IllegalArgumentException("kogitoprocinstanceid not found in the quotes response for CE " + cloudEvent);
    }

    public Optional<QuotesResponse> fetch(final String requestId) {
        return Optional.ofNullable(quotes.get(requestId));
    }

    public Map<String, QuotesResponse> list() {
        return quotes;
    }

}
