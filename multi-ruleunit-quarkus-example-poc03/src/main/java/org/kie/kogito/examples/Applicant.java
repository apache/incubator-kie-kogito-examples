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
package org.kie.kogito.examples;

public class Applicant {

    private String name;
    private int age;
    private int creditScore;
    private String occupationCode;
    private String previousOccupationCode;
    private String occupationCategory = null; // calculated by common rules

    public Applicant() {
    }

    public Applicant(String name, int age, int creditScore, String occupationCode, String previousOccupationCode) {
        super();
        this.name = name;
        this.age = age;
        this.creditScore = creditScore;
        this.occupationCode = occupationCode;
        this.previousOccupationCode = previousOccupationCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public String getPreviousOccupationCode() {
        return previousOccupationCode;
    }

    public void setPreviousOccupationCode(String previousOccupationCode) {
        this.previousOccupationCode = previousOccupationCode;
    }

    public String getOccupationCategory() {
        return occupationCategory;
    }

    public void setOccupationCategory(String occupationCategory) {
        this.occupationCategory = occupationCategory;
    }

}
