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
package org.kie.kogito.traffic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
public class TrafficProcessIT {

    public static final BigDecimal SPEED_LIMIT = new BigDecimal(100);

    @Test
    public void testTrafficViolationEmbeddedDecisionOnQuarkus() {
        testTrafficProcess("traffic", "12-345", 120d, "no", true, true);
        testTrafficProcess("traffic", "12-15", 140d, "yes", true, false);
        testTrafficProcess("traffic", "0-150", 140d, null, false, false);
    }

    private void testTrafficProcess(String processId, String driverId, Double speed, String suspended, boolean validLicense, boolean validatedLicense) {
        Map<String, Object> request = new HashMap<>();
        request.put("driverId", driverId);
        request.put("violation", new Violation("speed", SPEED_LIMIT, new BigDecimal(speed)));
        ValidatableResponse body = given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/" + processId)
                .then()
                .statusCode(201)
                .body("driver.ValidLicense", is(validLicense));
        if (suspended != null) {
            body.body("validated.ValidLicense", is(validatedLicense))
                    .body("validated.Suspended", is(suspended));
        } else {
            body.body("validated", nullValue());
        }
    }
}
