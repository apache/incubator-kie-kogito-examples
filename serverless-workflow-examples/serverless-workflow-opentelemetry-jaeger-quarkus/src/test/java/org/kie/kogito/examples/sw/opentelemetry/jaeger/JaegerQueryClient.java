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

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Minimal Jaeger Query API client for tests. Uses endpoints: - GET /api/services - GET
 * /api/traces?service=...&limit=... - GET /api/traces/{traceId}
 */
public class JaegerQueryClient {

    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public JaegerQueryClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        this.mapper = new ObjectMapper();
    }

    public List<String> listServices() {
        JsonNode root = getJson("/api/services");
        List<String> services = new ArrayList<>();
        JsonNode data = root.get("data");
        if (data != null && data.isArray()) {
            data.forEach(n -> services.add(n.asText()));
        }
        return services;
    }

    public Optional<String> findLatestTraceIdForService(String serviceName, int limit) {
        String qs = "?service=" + urlEncode(serviceName) + "&limit=" + limit;
        JsonNode root = getJson("/api/traces" + qs);
        JsonNode data = root.get("data");
        if (data == null || !data.isArray() || data.isEmpty()) {
            return Optional.empty();
        }
        // The API generally returns recent traces first, but we keep it simple:
        JsonNode first = data.get(0);
        JsonNode traceId = first.get("traceID");
        return traceId == null ? Optional.empty() : Optional.of(traceId.asText());
    }

    public JsonNode getTrace(String traceId) {
        return getJson("/api/traces/" + urlEncode(traceId));
    }

    private JsonNode getJson(String path) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(baseUrl + path)).timeout(Duration.ofSeconds(10))
                    .GET().build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new IllegalStateException(
                        "Jaeger API call failed: " + path + " status=" + resp.statusCode() + " body=" + resp.body());
            }
            return mapper.readTree(resp.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Jaeger API call failed: " + path, e);
        }
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
