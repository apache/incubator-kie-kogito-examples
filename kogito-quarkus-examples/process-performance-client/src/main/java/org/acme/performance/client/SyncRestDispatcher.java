/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.acme.performance.client;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

public class SyncRestDispatcher implements RequestDispatcher {

    private HttpRequest<Buffer> request;
    private WebClient webClient;
    private Vertx vertx;

    public SyncRestDispatcher(String processId) {
        this.vertx = Vertx.vertx();
        this.webClient = WebClient.create(vertx);
        this.request = webClient.request(HttpMethod.POST, 8080, "localhost", '/' + processId);
    }

    @Override
    public void dispatch(long delay, Consumer<Throwable> callback) {
        HttpResponse<Buffer> response = request.sendJsonAndAwait(Collections.singletonMap("delay", delay));
        if (response.statusCode() != 201) {
            callback.accept(new IllegalStateException(response.statusMessage()));
        }
    }

    @Override
    public void close() throws InterruptedException, ExecutionException {
        webClient.close();
        vertx.close();
    }

}
