package org.acme.serverless.loanbroker.aggregator.model;

import java.io.Serializable;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BankQuote implements Serializable {

    private double rate;
    private String bankId;

    public BankQuote() {
    }

    public BankQuote(final String bankId, final double rate) {
        this.bankId = bankId;
        this.rate = rate;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bankId, this.rate);
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
        BankQuote other = (BankQuote) obj;
        if (bankId == null) {
            if (other.bankId != null) {
                return false;
            }
        } else if (!bankId.equals(other.bankId)) {
            return false;
        }
        if (Double.doubleToLongBits(rate) != Double.doubleToLongBits(other.rate)) {
            return false;
        }
        return true;
    }

}
