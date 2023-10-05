/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.serverless.examples;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
