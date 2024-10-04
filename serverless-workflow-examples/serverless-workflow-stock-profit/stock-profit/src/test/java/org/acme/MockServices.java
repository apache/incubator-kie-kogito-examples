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
package org.acme;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;

public class MockServices implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        configureWiremockServer();
        return Map.of(
                "quarkus.rest-client.stock_svc_yaml.url", wireMockServer.baseUrl(),
                "quarkus.rest-client.stock_portfolio_svc_yaml.url", wireMockServer.baseUrl()
        );
    }

    private void configureWiremockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(get(urlEqualTo("/stock-price/KGTO"))
                                       .willReturn(aResponse()
                                                           .withStatus(201)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody("{ \"symbol\": \"KGTO\",  \"currentPrice\": \"110\" }")));

        wireMockServer.stubFor(get(urlMatching("/profit/KGTO\\?currentPrice=.+"))
                                       .willReturn(aResponse()
                                                           .withStatus(201)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody("{ \"symbol\": \"KGTO\",  \"profit\": \"10%\" }")));
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
