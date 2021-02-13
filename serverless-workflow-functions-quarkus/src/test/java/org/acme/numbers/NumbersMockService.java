/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.acme.numbers;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class NumbersMockService implements QuarkusTestResourceLifecycleManager{

    private WireMockServer wireMockServer;
    
    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start(); 
        stubFor(get(urlEqualTo("/numbers/random"))   
               .willReturn(aResponse()
                       .withHeader("Content-Type", "application/json")
                       .withBody("{\"randomNumber\": 1}")));
        
        stubFor(post(urlEqualTo("/numbers/1/multiplyByAndSum"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sum\": 34}")));
        
      return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

}
