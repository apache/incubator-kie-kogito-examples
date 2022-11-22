package org.acme.serverless.loanbroker.flow;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

/**
 * Mocks the Knative Sink service. Every event produced by the workflow will be
 * pushed to this server.
 */
public class SinkMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockServer.stubFor(post("/")
                .willReturn(aResponse().withBody("ok").withStatus(200)));

        // inject the endpoint to the HTTP outgoing smallrye messaging
        return Collections.singletonMap("mp.messaging.outgoing.kogito_outgoing_stream.url",
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
        ((LoanBrokerFlowTest) testInstance).sinkServer = wireMockServer;
    }

}
