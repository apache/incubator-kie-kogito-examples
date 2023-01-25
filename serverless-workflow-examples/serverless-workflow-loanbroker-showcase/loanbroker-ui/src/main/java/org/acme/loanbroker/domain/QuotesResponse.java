package org.acme.loanbroker.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuotesResponse implements Serializable {

    private String eventType;
    private String loanRequestId;
    private int amount;
    private int term;
    private Credit credit;
    private List<Quote> quotes = new ArrayList<>();

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public String getLoanRequestId() {
        return loanRequestId;
    }

    public void setLoanRequestId(String loanRequestId) {
        this.loanRequestId = loanRequestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuotesResponse that = (QuotesResponse) o;
        return amount == that.amount &&
                term == that.term &&
                Objects.equals(credit, that.credit) &&
                Objects.equals(quotes, that.quotes) &&
                Objects.equals(loanRequestId, that.loanRequestId) &&
                Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, term, credit, quotes, loanRequestId);
    }

    @Override
    public String toString() {
        return "QuotesResponse{" +
                "eventType='" + eventType + '\'' +
                ", loanRequestId='" + loanRequestId + '\'' +
                ", amount=" + amount +
                ", term=" + term +
                ", credit=" + credit +
                ", quotes=" + quotes +
                '}';
    }
}
