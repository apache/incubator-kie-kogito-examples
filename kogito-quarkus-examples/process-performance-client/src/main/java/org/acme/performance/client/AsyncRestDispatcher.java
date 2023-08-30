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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.WebClient;

public class AsyncRestDispatcher implements RequestDispatcher {

    private HttpRequest<Buffer> request;
    private WebClient webClient;
    private Vertx vertx;

    private static final Logger logger = LoggerFactory.getLogger(AsyncRestDispatcher.class);

    public AsyncRestDispatcher(String processId) {
        this.vertx = Vertx.vertx();
        this.webClient = WebClient.create(vertx);
        this.request = webClient.request(HttpMethod.POST, 8080, "localhost", '/' + processId);
    }

    @Override
    public void dispatch(long delay, Consumer<Throwable> ex) {
        request.sendJson(Collections.singletonMap("delay", delay)).subscribe().with(e -> logger.debug("Finished rest invocation {}", e), ex);
    }

    @Override
    public void close() throws InterruptedException, ExecutionException {
        webClient.close();
        vertx.close();
    }

}
