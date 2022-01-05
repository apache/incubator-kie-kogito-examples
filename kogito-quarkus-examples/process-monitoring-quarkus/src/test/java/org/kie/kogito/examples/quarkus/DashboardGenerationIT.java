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

package org.kie.kogito.examples.quarkus;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
public class DashboardGenerationIT {

    @Test
    @SuppressWarnings("unchecked")
    public void testDashboardsListIsAvailable() {
        List<String> dashboards = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when().get("/monitoring/dashboards/list.json").as(List.class);
        Assertions.assertEquals(3, dashboards.size());
        Assertions.assertTrue(dashboards.stream().anyMatch(s -> s.contains("demo.orderItems.json")));
        Assertions.assertTrue(dashboards.stream().anyMatch(s -> s.contains("Global.json")));
        Assertions.assertTrue(dashboards.stream().anyMatch(s -> s.contains("demo.orders.json")));
    }
}
