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
package org.kogito.examples.sw.github.workflow;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

/**
 * Mocked server to receive the produced messages by our SW.
 */
@ApplicationScoped
public class MessageSinkServer implements QuarkusTestResourceLifecycleManager {

    WireMockServer sinkServer;

    @Produces
    public WireMockServer getSinkServer() {
        return sinkServer;
    }

    @Override
    public Map<String, String> start() {
        sinkServer = new WireMockServer(WireMockConfiguration.options().port(8181));
        sinkServer.start();
        sinkServer.stubFor(post("/").willReturn(aResponse().withBody("ok").withStatus(200)));

        return null;
    }

    @Override
    public void stop() {
        if (sinkServer != null) {
            sinkServer.stop();
        }
    }
}
