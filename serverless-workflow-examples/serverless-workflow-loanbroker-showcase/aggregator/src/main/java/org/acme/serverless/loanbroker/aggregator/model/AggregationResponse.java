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
package org.acme.serverless.loanbroker.aggregator.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.acme.serverless.loanbroker.aggregator.IntegrationConstants;
import org.acme.serverless.loanbroker.aggregator.QuotesAggregationStrategy;
import org.apache.camel.Exchange;

public class AggregationResponse implements Serializable {

    private int quoteCount;
    private String kogitoProcessInstanceId;
    private LocalDateTime completitionDate;

    public static AggregationResponse fromExchange(final Exchange exchange) {
        final AggregationResponse response = new AggregationResponse();
        response.setCompletitionDate(LocalDateTime.now());
        response.setKogitoProcessInstanceId(
                exchange.getIn().getHeader(IntegrationConstants.KOGITO_FLOW_ID_HEADER).toString());
        response.setQuoteCount(
                Integer.valueOf(exchange.getIn().getHeader(QuotesAggregationStrategy.HEADER_QUOTES_COUNT).toString()));

        return response;
    }

    public LocalDateTime getCompletitionDate() {
        return completitionDate;
    }

    public void setCompletitionDate(LocalDateTime completitionDate) {
        this.completitionDate = completitionDate;
    }

    public String getKogitoProcessInstanceId() {
        return kogitoProcessInstanceId;
    }

    public void setKogitoProcessInstanceId(String kogitoProcessInstanceId) {
        this.kogitoProcessInstanceId = kogitoProcessInstanceId;
    }

    public int getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(int quoteCount) {
        this.quoteCount = quoteCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((completitionDate == null) ? 0 : completitionDate.hashCode());
        result = prime * result + ((kogitoProcessInstanceId == null) ? 0 : kogitoProcessInstanceId.hashCode());
        result = prime * result + quoteCount;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AggregationResponse other = (AggregationResponse) obj;
        if (completitionDate == null) {
            if (other.completitionDate != null) {
                return false;
            }
        } else if (!completitionDate.equals(other.completitionDate)) {
            return false;
        }
        if (kogitoProcessInstanceId == null) {
            if (other.kogitoProcessInstanceId != null) {
                return false;
            }
        } else if (!kogitoProcessInstanceId.equals(other.kogitoProcessInstanceId)) {
            return false;
        }
        if (quoteCount != other.quoteCount) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AggregationResponse [completitionDate=" + completitionDate + ", kogitoProcessInstanceId="
                + kogitoProcessInstanceId + ", quoteCount=" + quoteCount + "]";
    }

}
