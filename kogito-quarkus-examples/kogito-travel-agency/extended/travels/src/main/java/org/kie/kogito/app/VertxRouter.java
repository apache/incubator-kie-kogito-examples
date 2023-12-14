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
package org.kie.kogito.app;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static io.vertx.core.http.HttpMethod.GET;

@ApplicationScoped
public class VertxRouter {

    @Location("index")
    Template indexTemplate;

    @Inject
    Vertx vertx;

    void setupRouter(@Observes Router router) {
        router.route().handler(LoggerHandler.create());
        router.route().handler(FaviconHandler.create(vertx));
        router.route().handler(StaticHandler.create());
        String indexPage = indexTemplate.render();
        router.route(GET, "/").handler(ctx -> ctx.response().end(indexPage));
    }
}
