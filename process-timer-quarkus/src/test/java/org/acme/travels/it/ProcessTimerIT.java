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
package org.acme.travels.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.kie.kogito.process.JobServiceQuarkusTestResource;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@QuarkusTestResource(JobServiceQuarkusTestResource.class)
public class ProcessTimerIT {

    private static final String TIMERS = "timers";
    private static final String TIMERS_CYCLE = "timerscycle";
    private static final String TIMERS_ON_TASK = "timersOnTask";

    //Timers Tests
    @Test
    public void testTimers() {
        String id = createTimer(new Delay("PT02S"), TIMERS);
        Object id2 = getTimerById(id, TIMERS);
        assertThat(id).isEqualTo(id2);
        with().pollDelay(2, SECONDS)
                .atMost(3, SECONDS)
                .untilAsserted(() -> getTimerWithStatusCode(id, 204, TIMERS));
    }

    @Test
    public void testCancelTimer() {
        String id = createTimer(new Delay("PT030S"), TIMERS);
        Object id2 = deleteTimer(id, TIMERS);
        assertThat(id).isEqualTo(id2);
        getTimerWithStatusCode(id, 204, TIMERS);
    }

    //Cycle Timers Tests
    @Test
    public void testTimerCycle() {
        String id = createTimer(new Delay("R2/PT1S"), TIMERS_CYCLE);
        String id2 = getTimerById(id, TIMERS_CYCLE);
        assertThat(id).isEqualTo(id2);
        with().pollDelay(2, SECONDS)
                .atMost(3, SECONDS)
                .untilAsserted(() -> getTimerWithStatusCode(id, 204, TIMERS));
    }

    @Test
    public void testDeleteTimerCycle() {
        String id = createTimer(new Delay("R20/PT1S"), TIMERS_CYCLE);
        String id2 = getTimerById(id, TIMERS_CYCLE);
        assertThat(id).isEqualTo(id2);
        deleteTimer(id, TIMERS_CYCLE);
    }

    //Boundary Timers Tests
    @Test
    public void testBoundaryTimersOnTask() {
        String id = createTimer(new Delay("PT02S"), TIMERS_ON_TASK);
        String id2 = getTimerById(id, TIMERS_ON_TASK);
        assertThat(id).isEqualTo(id2);
        with().pollDelay(2, SECONDS)
                .atMost(3, SECONDS)
                .untilAsserted(() -> getTimerWithStatusCode(id, 204, TIMERS_ON_TASK));
    }

    @Test
    public void testDeleteBoundaryTimersOnTask() {
        String id = createTimer(new Delay("PT030S"), TIMERS_ON_TASK);
        String id2 = getTimerById(id, TIMERS_ON_TASK);
        assertThat(id).isEqualTo(id2);
        deleteTimer(id, TIMERS_ON_TASK);
    }

    private ValidatableResponse getTimerWithStatusCode(String id, int code, String path) {
        return given()
                .accept(ContentType.JSON)
                .when()
                .get("/" + path + "/{id}", id)
                .then()
                .statusCode(code);
    }

    private String createTimer(Delay delay, String path) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(delay)
                .when()
                .post("/" + path)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    private String getTimerById(String id, String path) {
        return getTimerWithStatusCode(id, 200, path)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    private Object deleteTimer(String id, String path) {
        return given()
                .accept(ContentType.JSON)
                .when()
                .delete("/" + path + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    /**
     * Simple bean class to send as body on the requests
     */
    private class Delay {

        private String delay;

        public Delay(String delay) {
            this.delay = delay;
        }

        public String getDelay() {
            return delay;
        }
    }
}
