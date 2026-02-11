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

package org.kie.kogito.examples.sw.opentelemetry.jaeger.helper;

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
 * Minimal Jaeger Query HTTP API client for tests. 
 * Endpoints used: 
 * - GET /api/services 
 * - GET /api/traces?service=<service>&limit=<n> 
 * - GET /api/traces/<traceId>
 */
public class JaegerQueryClient {

    private final String baseUrl; 
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public JaegerQueryClient(String baseUrl) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        this.mapper = new ObjectMapper();
    }

    public List<String> listServices() {
        JsonNode root = getJson("/api/services");
        JsonNode data = root.get("data");
        List<String> services = new ArrayList<>();
        if (data != null && data.isArray()) {
            data.forEach(n -> services.add(n.asText()));
        }
        return services;
    }

    /**
     * Find traces for a service using Jaeger's HTTP query API. 
     */
    public JsonNode findTracesByService(String serviceName, int limit) {
        return findTracesByService(serviceName, limit, "1h");
    }

    /**
     * Same as above but configurable lookback
     */
    public JsonNode findTracesByService(String serviceName, int limit, String lookback) {
        String serviceEnc = URLEncoder.encode(serviceName, StandardCharsets.UTF_8);
        String lookbackEnc = URLEncoder.encode(lookback, StandardCharsets.UTF_8);

        // Jaeger supports: service, limit, lookback.
        // You can also add start/end, tags, operation, minDuration, maxDuration if needed.
        String url = baseUrl + "/api/traces?service=" + serviceEnc + "&limit=" + limit + "&lookback=" + lookbackEnc;

        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("Accept", "application/json")
                .build();

        HttpResponse<String> resp = send(req);

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IllegalStateException("Jaeger findTracesByService failed: HTTP " + resp.statusCode() + " url="
                    + url + " body=" + safeBody(resp.body()));
        }

        try {
            return mapper.readTree(resp.body());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse Jaeger response JSON from " + url + ": " + e.getMessage(),
                    e);
        }
    }

    public List<String> findTraceIdsForService(String serviceName, int limit) {
        JsonNode resp = findTracesByService(serviceName, limit); // implement using your existing HTTP call
        JsonNode data = resp.get("data");
        if (data == null || !data.isArray() || data.isEmpty()) {
            return List.of();
        }

        List<String> ids = new ArrayList<>();
        for (JsonNode item : data) {
            JsonNode traceID = item.get("traceID");
            if (traceID != null && !traceID.asText().isBlank()) {
                ids.add(traceID.asText());
            }
        }
        return ids;
    }

    public Optional<String> findLatestTraceIdForService(String serviceName, int limit) {
        List<String> ids = findTraceIdsForService(serviceName, limit);
        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }

    public JsonNode getTrace(String traceId) {
        return getJson("/api/traces/" + urlEncode(traceId));
    }

    private JsonNode getJson(String path) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(baseUrl + path)).timeout(Duration.ofSeconds(10))
                    .GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                throw new IllegalStateException("Jaeger API call failed: " + path + " status=" + resp.statusCode()
                        + " body=" + truncate(resp.body(), 400));
            }
            return mapper.readTree(resp.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Jaeger API call failed: " + path, e);
        }
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be null/blank");
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private HttpResponse<String> send(HttpRequest req) {
        try {
            return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed calling Jaeger: " + req.uri() + " -> " + e.getMessage(), e);
        }
    }

    private static String safeBody(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 500 ? body.substring(0, 500) + "..." : body;
    }
}
