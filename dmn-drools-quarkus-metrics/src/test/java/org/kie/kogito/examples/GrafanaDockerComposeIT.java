/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.examples;

import java.io.File;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

@Testcontainers
public class GrafanaDockerComposeIT {

    private static final String GRAFANA_URL = "http://localhost:3000";
    private static final String PROMETHEUS_PRIVATE_URL = "http://prometheus:9090";

    @Container
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("./docker-compose.yml"))
                    .withExposedService("grafana_1", 3000, Wait.forHttp("/")
                            .forStatusCode(200));

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
}
