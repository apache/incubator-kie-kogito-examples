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

import java.io.File;
import java.net.URISyntaxException;
import java.time.Duration;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GrafanaDockerComposeIT {

    private static final int STARTUP_MINUTES_TIMEOUT = 8;
    private static final int GRAFANA_PORT = 3000;
    private static final int PROMETHEUS_PORT = 9090;
    private static final int KOGITO_APPLICATION_PORT = 8080;
    private static final String GRAFANA_URL = "http://localhost:" + GRAFANA_PORT;
    private static final String PROMETHEUS_PRIVATE_URL = "http://prometheus:" + PROMETHEUS_PORT;
    private static final String KOGITO_APPLICATION_URL = "http://localhost:" + KOGITO_APPLICATION_PORT;


    @Container
    public static DockerComposeContainer environment;

    static {
        try {
            environment = new DockerComposeContainer(new File(GrafanaDockerComposeIT.class.getClassLoader().getResource("./docker-compose.yml").toURI()))
                    .withExposedService("grafana_1", GRAFANA_PORT, Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(STARTUP_MINUTES_TIMEOUT)))
                    .withExposedService("hello_1", KOGITO_APPLICATION_PORT, Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(STARTUP_MINUTES_TIMEOUT)))
                    .withExposedService("prometheus_1", PROMETHEUS_PORT, Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(STARTUP_MINUTES_TIMEOUT)));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    void setup() {
        environment.start();
    }

    @AfterAll
    void cleanup() {
        environment.stop();
    }

    @Test
    public void testPrometheusDataSource() {
        given()
                .baseUri(GRAFANA_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/datasources")
                .then()
                .statusCode(200)
                .body("type", hasItem("prometheus"))
                .body("url", hasItem(PROMETHEUS_PRIVATE_URL));
    }

    @Test
    public void testGrafanaDashboards() {
        given()
                .baseUri(GRAFANA_URL)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/search")
                .then()
                .statusCode(200)
                .body("title", hasItem("hello - Operational Dashboard"))
                .body("title", hasItem("LoanEligibility - Domain Dashboard"))
                .body("title", hasItem("LoanEligibility - Operational Dashboard"));
    }

    @Test
    public void testKogitoContainerIsDeployedAndResponsive() {
        String body = "{\"Client\": {\"age\": 43,\"salary\": 1950, \"existing payments\": 100}, \"Loan\": {\"duration\": 15,\"installment\": 180}, \"SupremeDirector\" : \"Yes\", \"Bribe\": 1000}";

        given()
                .baseUri(KOGITO_APPLICATION_URL)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/LoanEligibility")
                .then()
                .statusCode(200);
    }
}
