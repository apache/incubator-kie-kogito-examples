package org.kie.kogito.serverless;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class OperationsMockService implements QuarkusTestResourceLifecycleManager {

    private WireMockServer subtractionService;
    private WireMockServer multiplicationService;

    @Override
    public Map<String, String> start() {
        multiplicationService =
                this.startServer(8080,
                                 "{\"multiplication\": { \"leftElement\": \"68.0\", \"rightElement\": \"0.5556\", \"product\": \"37.808\" }}");
        subtractionService =
                this.startServer(8181,
                                 "{\"subtraction\": { \"leftElement\": \"100\", \"rightElement\": \"32\", \"difference\": \"68.0\" }}");
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
                        .willReturn(aResponse()
                                            .withHeader("Content-Type", "application/json")
                                            .withBody(response)));
        return server;
    }
}
