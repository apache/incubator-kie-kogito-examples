/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.Map;

import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class CallBackMockService implements QuarkusTestResourceLifecycleManager {

    private WireMockServer callbackService;

    @Override
    public Map<String, String> start() {
        callbackService =
                this.startServer(8181);
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (callbackService != null) {
            callbackService.stop();
        }
    }

    private WireMockServer startServer(final int port) {
        final WireMockServer server = new WireMockServer(port);
        server.start();
        server.stubFor(post(urlEqualTo("/event"))
                .willReturn(aResponse()
                        .withStatus(200)));
        return server;
    }
}
