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

package org.kie.kogito.examples.sw.opentelemetry.jaeger.persistence;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.kie.kogito.examples.sw.opentelemetry.jaeger.helper.JaegerHelper.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.kie.kogito.examples.sw.opentelemetry.jaeger.JaegerTestResource;

@QuarkusIntegrationTest
@QuarkusTestResource(PostgresTestResource.class)
@QuarkusTestResource(JaegerTestResource.class)
public class PersistWait01StartIT {

    private static final String START_ENDPOINT = "/persistWait";

    @Test
    void start_and_persist_instanceId() {
        // Start workflow so it reaches the waiting state
        JsonNode started = given().header("X-TRANSACTION-ID", "txn-persist-001")
                                  .header("X-TRACKER-correlation_id", "corr-persist-001")
				  .contentType("application/json")
				  .body("{}")
                          .when()
			          .post(START_ENDPOINT)
		          .then()
			          .statusCode(anyOf(is(200), is(201))).extract().as(JsonNode.class);

        String pid = started.hasNonNull("id") ? started.get("id").asText() : null;
        if (pid == null || pid.isBlank()) {
            throw new AssertionError("Start response did not contain 'id'. Response: " + started);
        }

        JsonNode trace = waitForTraceWithTracker("sonataflow.tracker.correlation_id", "corr-persist-001");
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.id", "persistWait");
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.instance.id", pid);

        assertWorkflowHasAnyState(trace, "printWaitMessage");

        // Verify it exists immediately (before restart in the other test)
        given().when().get(START_ENDPOINT + "/" + pid).then().statusCode(200);

        PersistWaitPidStore.write(pid);
    }
}
