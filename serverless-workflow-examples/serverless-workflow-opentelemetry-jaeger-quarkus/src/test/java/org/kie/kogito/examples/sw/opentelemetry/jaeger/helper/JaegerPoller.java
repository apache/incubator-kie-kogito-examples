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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Simple polling utilities to avoid sleeps in tests and to handle eventual
 * consistency in Jaeger.
 */
public final class JaegerPoller {

    private JaegerPoller() {
    }

    public static void waitForService(JaegerQueryClient jaeger, String serviceName, Duration timeout,
            Duration pollInterval) {
        Instant deadline = Instant.now().plus(timeout);

        while (Instant.now().isBefore(deadline)) {
            List<String> services = jaeger.listServices();
            if (services.contains(serviceName)) {
                return;
            }
            sleep(pollInterval);
        }
        throw new AssertionError(
                "Timed out waiting for Jaeger service '" + serviceName + "' to appear in /api/services");
    }

    /**
     * Waits until Jaeger returns a trace ID for the given service.
     *
     * @return the traceId
     */
    public static String waitForAnyTrace(JaegerQueryClient jaeger, String serviceName, int limit, Duration timeout,
            Duration pollInterval) {

        Instant deadline = Instant.now().plus(timeout);

        while (Instant.now().isBefore(deadline)) {
            Optional<String> traceId = jaeger.findLatestTraceIdForService(serviceName, limit);
            if (traceId.isPresent()) {
                return traceId.get();
            }
            sleep(pollInterval);
        }
        throw new AssertionError("Timed out waiting for any trace for service '" + serviceName + "' in /api/traces");
    }

    private static void sleep(Duration d) {
        try {
            Thread.sleep(Math.max(1, d.toMillis()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Polling interrupted", e);
        }
    }
}
