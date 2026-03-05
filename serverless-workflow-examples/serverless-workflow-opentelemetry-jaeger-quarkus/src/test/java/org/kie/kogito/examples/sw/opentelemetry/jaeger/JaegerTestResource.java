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

package org.kie.kogito.examples.sw.opentelemetry.jaeger;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Starts a Jaeger all-in-one container (with OTLP ingestion enabled) for tests. Exposes: - Jaeger Query/UI: 16686 -
 * OTLP gRPC: 4317 - OTLP HTTP: 4318 Provides Quarkus config so the app exports traces to Jaeger over OTLP.
 */
public class JaegerTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = Logger.getLogger(JaegerTestResource.class);

    // Pin a specific version for CI stability (avoid "latest")
    private static final String JAEGER_IMAGE = "jaegertracing/all-in-one:1.54";

    private static final int JAEGER_QUERY_PORT = 16686;
    private static final int OTLP_GRPC_PORT = 4317;
    private static final int OTLP_HTTP_PORT = 4318;

    private GenericContainer<?> jaeger;

    @Override
    public Map<String, String> start() {
        jaeger = new GenericContainer<>(JAEGER_IMAGE)
                .withExposedPorts(JAEGER_QUERY_PORT, OTLP_GRPC_PORT, OTLP_HTTP_PORT)
                // Enable OTLP ingestion in Jaeger all-in-one
                .withEnv("COLLECTOR_OTLP_ENABLED", "true")
                // Wait until Jaeger query API is responsive (better than only port-open)
                .waitingFor(Wait.forHttp("/api/services").forPort(JAEGER_QUERY_PORT).forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(60)));

        jaeger.start();

        String host = jaeger.getHost();
        Integer mappedOtlpGrpc = jaeger.getMappedPort(OTLP_GRPC_PORT);
        Integer mappedOtlpHttp = jaeger.getMappedPort(OTLP_HTTP_PORT);
        Integer mappedQuery = jaeger.getMappedPort(JAEGER_QUERY_PORT);

        // OTLP endpoint for Quarkus OTel exporter.
        // For gRPC, Quarkus typically expects http://host:port and protocol=grpc.
        String otlpGrpcEndpoint = "http://" + host + ":" + mappedOtlpGrpc;

        // Jaeger Query URL (handy for tests that call the Jaeger HTTP API)
        String jaegerQueryBaseUrl = "http://" + host + ":" + mappedQuery;

        LOGGER.infof("Jaeger started: query=%s, otlpGrpc=%s, otlpHttp=http://%s:%d", jaegerQueryBaseUrl,
                otlpGrpcEndpoint, host, mappedOtlpHttp);

        Map<String, String> cfg = new HashMap<>();

        // --- OTel export configuration (set both common variants to be safe across Quarkus lines) ---
        // cfg.put("quarkus.otel.traces.exporter", "otlp");

        // Preferred OTLP exporter config (Quarkus 3.x commonly uses these)
        cfg.put("quarkus.otel.exporter.otlp.endpoint", otlpGrpcEndpoint);
        cfg.put("quarkus.otel.exporter.otlp.protocol", "grpc");

        // Some setups split traces endpoint (harmless if ignored)
        cfg.put("quarkus.otel.exporter.otlp.traces.endpoint", otlpGrpcEndpoint);
        cfg.put("quarkus.otel.exporter.otlp.traces.protocol", "grpc");

        // OpenTelemetry SDK autoconfigure
        cfg.put("otel.metrics.exporter", "none");

        // Optional: make Jaeger query base URL available to the tests
        cfg.put("test.jaeger.query.base-url", jaegerQueryBaseUrl);

        return cfg;
    }

    @Override
    public void stop() {
        if (jaeger != null) {
            try {
                jaeger.stop();
            } catch (Exception e) {
                LOGGER.warn("Failed to stop Jaeger container cleanly", e);
            } finally {
                jaeger = null;
            }
        }
    }

    /**
     * Convenience accessors for tests (optional). Note: These require the resource instance to be started in the same
     * JVM.
     */
    public String getJaegerQueryBaseUrl() {
        if (jaeger == null) {
            throw new IllegalStateException("Jaeger container is not started");
        }
        return "http://" + jaeger.getHost() + ":" + jaeger.getMappedPort(JAEGER_QUERY_PORT);
    }

    public String getOtlpGrpcEndpoint() {
        if (jaeger == null) {
            throw new IllegalStateException("Jaeger container is not started");
        }
        return "http://" + jaeger.getHost() + ":" + jaeger.getMappedPort(OTLP_GRPC_PORT);
    }
}
