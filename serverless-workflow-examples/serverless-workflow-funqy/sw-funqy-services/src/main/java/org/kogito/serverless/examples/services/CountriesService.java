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
package org.kogito.serverless.examples.services;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kogito.serverless.examples.input.Country;

@ApplicationScoped
public class CountriesService {

    private List<Country> availableCountries = new ArrayList<>();
    private Country errorCountry = new Country("N/A", "N/A", "N/A");

    public CountriesService() {
        availableCountries.add(
                new Country("Brazil", "Brasilia", "South America"));
        availableCountries.add(
                new Country("USA", "Washington, D.C.", "North America"));
        availableCountries.add(
                new Country("Serbia", "Belgrade", "Europe"));
        availableCountries.add(
                new Country("Germany", "Berlin", "Europe"));
    }

    public Country getCountry(String countryName) {
        return availableCountries.stream()
                .filter(c -> c.getName().equals(countryName))
                .findFirst()
                .orElse(errorCountry);
    }

}
