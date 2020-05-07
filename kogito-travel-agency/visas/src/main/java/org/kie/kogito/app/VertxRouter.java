/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.app;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static io.vertx.core.http.HttpMethod.GET;
import static java.nio.charset.StandardCharsets.UTF_8;

@ApplicationScoped
public class VertxRouter {

    @Inject
    @ConfigProperty(name = "kogito.dataindex.http.url", defaultValue = "http://localhost:8180")
    String dataIndexHttpURL;

    @Inject
    @ConfigProperty(name = "kogito.dataindex.ws.url", defaultValue = "ws://localhost:8180")
    String dataIndexWsURL;

    @Inject
    Vertx vertx;

    private Buffer resource;

    @PostConstruct
    public void init() {
        try {
            resource = Buffer.buffer(vertx.fileSystem()
                                             .readFileBlocking("META-INF/resources/index.html")
                                             .toString(UTF_8)
                                             .replace("__GRAPHQL_HTTP_ENDPOINT__", "\"" + dataIndexHttpURL + "/graphql\"")
                                             .replace("__GRAPHQL_WS_ENDPOINT__", "\"" + dataIndexWsURL + "/graphql\""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void setupRouter(@Observes Router router) {
        router.route().handler(LoggerHandler.create());
        router.route().handler(FaviconHandler.create());
        router.route().handler(StaticHandler.create());
        router.route(GET, "/").handler(ctx -> ctx.response().end(resource));
    }
}