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
package org.kie.kogito.examples;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.Topic;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
public class TopicsInformationTest {

    @Test
    void verifyTopicsInformation() {
        Topic expectedIncomingTopic = new Topic("kogito_incoming_stream", ChannelType.INCOMING);
        expectedIncomingTopic.setEventsMeta(Collections.singletonList(new CloudEventMeta("DecisionRequest", "", EventKind.CONSUMED)));

        Set<CloudEventMeta> expectedOutgoingEventMeta = new HashSet<>();
        expectedOutgoingEventMeta.add(new CloudEventMeta("DecisionResponse", "Traffic+Violation", EventKind.PRODUCED));
        expectedOutgoingEventMeta.add(new CloudEventMeta("DecisionResponse", "Traffic+Violation/FineService", EventKind.PRODUCED));
        expectedOutgoingEventMeta.add(new CloudEventMeta("DecisionResponseFull", "Traffic+Violation", EventKind.PRODUCED));
        expectedOutgoingEventMeta.add(new CloudEventMeta("DecisionResponseFull", "Traffic+Violation/FineService", EventKind.PRODUCED));
        expectedOutgoingEventMeta.add(new CloudEventMeta("DecisionResponseError", "Traffic+Violation", EventKind.PRODUCED));
        expectedOutgoingEventMeta.add(new CloudEventMeta("DecisionResponseError", "Traffic+Violation/FineService", EventKind.PRODUCED));
        expectedOutgoingEventMeta.add(new CloudEventMeta("DecisionResponseError", "__UNKNOWN_SOURCE__", EventKind.PRODUCED));
        Topic expectedOutgoingTopic = new Topic("kogito_outgoing_stream", ChannelType.OUTGOING);
        expectedOutgoingTopic.setEventsMeta(expectedOutgoingEventMeta);

        List<Topic> topics = Arrays.asList(given().get("/messaging/topics").as(Topic[].class));
        assertThat(topics, notNullValue());
        assertThat(topics, hasItem(expectedIncomingTopic));
        assertThat(topics, hasItem(expectedOutgoingTopic));
    }
}
