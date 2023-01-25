package org.acme.loanbroker.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Objects;

public class Credit {
    private int score;
    private int history;
    @JsonProperty
    private String SSN;

    public Credit() {
    }

    public Credit(int score, int history, String SSN) {
        this.score = score;
        this.history = history;
        this.SSN = SSN;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    @JsonGetter("SSN")
    public String getSSN() {
        return SSN;
    }

    @JsonSetter("SSN")
    public void setSSN(String SSN) {
        this.SSN = SSN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Credit credit = (Credit) o;
        return score == credit.score && history == credit.history && Objects.equals(SSN, credit.SSN);
    }

    @Override public int hashCode() {
        return Objects.hash(score, history, SSN);
    }
}
