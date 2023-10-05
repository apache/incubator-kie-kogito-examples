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
package org.acme.serverless.examples.functions;

import javax.inject.Inject;

import org.acme.serverless.examples.services.PopulationService;
import org.acme.serverless.examples.input.Country;
import org.acme.serverless.examples.services.CountriesService;

import io.quarkus.funqy.Funq;

public class PopulationFunction {
    @Inject
    CountriesService countriesService;

    @Inject
    PopulationService populationService;

    @Funq
    public Country population(Country country) {
        return populationService.getPopulation(
                countriesService.getCountry(country.name));
    }
}
