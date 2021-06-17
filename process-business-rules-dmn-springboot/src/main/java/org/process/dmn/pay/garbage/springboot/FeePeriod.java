/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.process.dmn.pay.garbage.springboot;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class FeePeriod implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    private java.time.Period lastingFor;
    private java.time.LocalDate startingFrom;

    public FeePeriod() {
    }

    public java.time.Period getLastingFor() {
        return this.lastingFor;
    }

    public void setLastingFor(java.time.Period lastingFor) {
        this.lastingFor = lastingFor;
    }

    public java.time.LocalDate getStartingFrom() {
        return this.startingFrom;
    }

    public void setStartingFrom(java.time.LocalDate startingFrom) {
        this.startingFrom = startingFrom;
    }

    public FeePeriod(java.time.Period lastingFor,
            java.time.LocalDate startingFrom) {
        this.lastingFor = lastingFor;
        this.startingFrom = startingFrom;
    }

}