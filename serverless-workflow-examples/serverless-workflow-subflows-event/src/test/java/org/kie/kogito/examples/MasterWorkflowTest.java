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
package org.kie.kogito.examples;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.impl.ByteArrayCloudEventMarshaller;
import org.kie.kogito.event.impl.NoOpCloudEventMarshaller;
import org.kie.kogito.event.impl.NoOpEventMarshaller;
import org.kie.kogito.event.impl.StringCloudEventMarshaller;
import org.kie.kogito.event.impl.StringEventMarshaller;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasLength;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;


@QuarkusTest
class MasterWorkflowTest {
	
	private static final CloudEventMarshaller<?> marshaller = new StringCloudEventMarshaller(ObjectMapperFactory.get());

    @Test
    void testPartialParallelRest() throws IOException {
        String id = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{}").when()
                .post("/master")
                .then()
                .statusCode(201).extract().path("id");
       sendEvent (id, "executeA");
       sendEvent (id, "executeB");
       waitForFinish("master", id, Duration.ofSeconds(10));
    }
    
    static void waitForFinish(String flowName, String id, Duration duration) {
        await("dead").atMost(duration)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get("/" + flowName + "/{id}", id)
                        .then()
                        .statusCode(404));
    }
    
    private void sendEvent(String id, String eventType) throws IOException {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(marshaller.marshall(buildCloudEvent(id, eventType, marshaller)))
                    .post("/" + eventType)
                    .then()
                    .statusCode(202);
    }
    
    static CloudEvent buildCloudEvent(String id, Optional<String> businessKey, String type, CloudEventMarshaller<?> marshaller) {
        io.cloudevents.core.v1.CloudEventBuilder builder = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(type)
                .withTime(OffsetDateTime.now())
                .withData(marshaller.cloudEventDataFactory().apply(Collections.singletonMap("param4", "Additional argument")));
        businessKey.ifPresentOrElse(key -> builder.withExtension(CloudEventExtensionConstants.BUSINESS_KEY, key), () -> builder.withExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, id));
        return builder.build();
    }

    static CloudEvent buildCloudEvent(String id, String type, CloudEventMarshaller<?> marshaller) {
        return buildCloudEvent(id, Optional.empty(), type, marshaller);
    }

}
