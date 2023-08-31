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
package org.acme.numbers;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.node.IntNode;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class NumbersMockService implements QuarkusTestResourceLifecycleManager {

    private static final WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());

    @Override
    public Map<String, String> start() {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
        }
        wireMockServer.stubFor(get(urlEqualTo("/numbers/random")).willReturn(ok().withHeader("Content-Type", "application/json").withJsonBody(new IntNode(1))));
        wireMockServer.stubFor(post(urlEqualTo("/numbers/1/multiplyByAndSum")).willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withJsonBody(new IntNode(34))));
        return Collections.singletonMap("wiremock.port", Integer.toString(wireMockServer.port()));
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public static int serverPort() {
        return wireMockServer.port();
    }
}