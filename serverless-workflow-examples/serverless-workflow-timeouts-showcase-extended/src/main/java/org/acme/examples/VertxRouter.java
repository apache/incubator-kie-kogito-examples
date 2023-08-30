package org.acme.examples;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;

@ApplicationScoped
public class VertxRouter {

    public void setupRouter(@Observes Router router) {
        // send get requests on the root path o the index.html too.
        router.route(HttpMethod.GET, "/").order(Integer.MIN_VALUE).handler(ctx -> ctx.reroute("/index.html"));
    }
}
