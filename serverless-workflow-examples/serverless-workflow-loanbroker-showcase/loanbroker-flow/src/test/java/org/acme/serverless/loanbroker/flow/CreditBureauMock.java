package org.acme.serverless.loanbroker.flow;

import java.util.Collections;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

public class CreditBureauMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockServer.stubFor(get(urlPathMatching("/*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBody("{ \"SSN\":\"123-45-6789\",\"score\":470,\"history\":4}}")
                        .withStatus(200)));

        // inject the endpoint to the generated RESTClient Stub
        return Collections.singletonMap("quarkus.rest-client.credit_bureau_yaml.url",
                wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Override
    public void inject(Object testInstance) {
        ((LoanBrokerFlowTest) testInstance).creditBureauServer = wireMockServer;
    }

}
