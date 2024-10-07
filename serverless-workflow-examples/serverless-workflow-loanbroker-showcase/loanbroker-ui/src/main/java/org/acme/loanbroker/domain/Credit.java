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
