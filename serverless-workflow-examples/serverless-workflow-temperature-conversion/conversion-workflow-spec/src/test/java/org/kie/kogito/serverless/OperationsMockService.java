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
package org.kie.kogito.serverless;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Collections;
import java.util.Map;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

public class OperationsMockService implements QuarkusTestResourceLifecycleManager {

    private WireMockServer subtractionService;
    private WireMockServer multiplicationService;

    @Override
    public Map<String, String> start() {
        multiplicationService =
                this.startServer(8282,
                        "{  \"product\": 37.808 }");
        subtractionService =
                this.startServer(8181,
                        "{ \"difference\": 68.0 }");
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (subtractionService != null) {
            subtractionService.stop();
        }
        if (multiplicationService != null) {
            multiplicationService.stop();
        }
    }

    private WireMockServer startServer(final int port, final String response) {
        final WireMockServer server = new WireMockServer(port);
        server.start();
        server.stubFor(post(urlEqualTo("/"))
                .withHeader(CloudEventExtensionConstants.PROCESS_ID, WireMock.matching(".*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        return server;
    }
}
