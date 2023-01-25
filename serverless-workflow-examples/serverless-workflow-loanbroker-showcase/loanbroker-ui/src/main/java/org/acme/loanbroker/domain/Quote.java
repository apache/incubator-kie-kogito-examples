package org.acme.loanbroker.domain;

import java.util.Objects;

public class Quote {
    private double rate;
    private String bankId;

    public Quote() {
    }

    public Quote(double rate, String bankId) {
        this.bankId = bankId;
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Quote quote = (Quote) o;
        return Double.compare(quote.rate, rate) == 0 && Objects.equals(bankId, quote.bankId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rate, bankId);
    }
}
