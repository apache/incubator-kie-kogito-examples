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
package org.kie.kogito.examples.onboarding;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Address implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    private java.lang.String street;
    private java.lang.String city;
    private java.lang.String zipCode;
    private java.lang.String country;

    public Address() {
    }

    public java.lang.String getStreet() {
        return this.street;
    }

    public void setStreet(java.lang.String street) {
        this.street = street;
    }

    public java.lang.String getCity() {
        return this.city;
    }

    public void setCity(java.lang.String city) {
        this.city = city;
    }

    public java.lang.String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(java.lang.String zipCode) {
        this.zipCode = zipCode;
    }

    public java.lang.String getCountry() {
        return this.country;
    }

    public void setCountry(java.lang.String country) {
        this.country = country;
    }

    public Address(java.lang.String street, java.lang.String city,
            java.lang.String zipCode, java.lang.String country) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

}