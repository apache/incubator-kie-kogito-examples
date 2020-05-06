/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.acme.travels;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class KeycloakContainerResource extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakContainerResource.class);
    private static final String KEYCLOAK_IMAGE = System.getProperty("container.image.keycloak");

    private GenericContainer keycloak;

    @Override
    protected void before() {
        keycloak = new FixedHostPortGenericContainer(KEYCLOAK_IMAGE).withFixedExposedPort(8281, 8080)
                .withEnv("KEYCLOAK_USER", "admin").withEnv("KEYCLOAK_PASSWORD", "admin")
                .withEnv("KEYCLOAK_IMPORT", "/tmp/realm.json")
                .withClasspathResourceMapping("kogito-realm.json", "/tmp/realm.json", BindMode.READ_ONLY)
                .withLogConsumer(new Slf4jLogConsumer(LOGGER)).waitingFor(Wait.forHttp("/auth"));
        keycloak.start();
    }
    @Override
    protected void after() {
        keycloak.stop();
    }

}
