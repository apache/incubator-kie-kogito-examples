package org.acme.loanbroker;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.acme.loanbroker.domain.QuotesResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

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
