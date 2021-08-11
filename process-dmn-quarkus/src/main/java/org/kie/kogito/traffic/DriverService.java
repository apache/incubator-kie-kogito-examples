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

package org.kie.kogito.traffic;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DriverService {

    private static Logger LOGGER = LoggerFactory.getLogger(DriverService.class);

    public Driver getDriver(String driverId) {
        LOGGER.info("Get Driver Information for id = {}", driverId);
        //Could call an external service, database, etc...
        return new Driver(driverId, "Arthur", "SP", "Campinas", new Random().ints(10, 20).findAny().getAsInt(), 30);
    }
}
