/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.kie.kogito.**"})
public class FlightSeatingApplication {

    public static void main(String[] args) {
        // Spring-boot dev tools class loader cannot detect generated sources
        // so we need to disable restart if we want to take advantage of live-reload
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(FlightSeatingApplication.class, args);
    }
}
