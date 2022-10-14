package org.acme.serverless.loanbroker.aggregator;

import java.util.List;

import org.acme.serverless.loanbroker.aggregator.model.BankQuote;
import org.apache.camel.Processor;

/**
 * Interface that defines the internal persistence storage for the aggregated
 * quotes.
 * Should be split into Processor and Repository in real life implementations to
 * keep the separation of concerns.
 */
public interface QuotesRepositoryProcessor extends Processor {

    List<BankQuote> fetchQuotesByInstanceId(final String instanceId);

    /**
     * Remove all entries
     */
    void clear();

}
