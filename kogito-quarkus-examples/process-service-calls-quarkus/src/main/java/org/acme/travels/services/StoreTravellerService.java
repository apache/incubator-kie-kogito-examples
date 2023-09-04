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
package org.acme.travels.services;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.travels.quarkus.Traveller;

@ApplicationScoped
public class StoreTravellerService {

    private Map<String, Traveller> store = new HashMap<>();

    public boolean storeTraveller(Traveller traveller) {
        Traveller stored = store.putIfAbsent(traveller.getEmail(), traveller);

        if (stored == null) {
            return true;
        }

        return false;
    }
}
