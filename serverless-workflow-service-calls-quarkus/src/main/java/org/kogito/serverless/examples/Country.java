package org.kogito.serverless.examples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {

    public String name;
    public String alpha2Code;
    public String capital;
    public String region;
    public String population;
    public String classifier;
    public List<Currency> currencies;

    public String getName() {
        return name;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public String getCapital() {
        return capital;
    }

    public String getClassifier() {
        return classifier;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public String getRegion() {
        return region;
    }

    public String getPopulation() {
        return population;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public static class Currency {
        public String code;
        public String name;
        public String symbol;

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getSymbol() {
            return symbol;
        }
    }

}
