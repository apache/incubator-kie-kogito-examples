package org.kie.kogito.app;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.vertx.web.Route;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static io.vertx.core.http.HttpMethod.GET;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URL;

@ApplicationScoped
public class VertxRouter {

    @Inject
    @ConfigProperty(name = "kogito.dataindex.url", defaultValue = "http://localhost:8180")
    String dataIndexURL;

    @Inject
    Vertx vertx;

    private String resource;

    @PostConstruct
    public void init() {
        try {
            String scheme = new URL(dataIndexURL).toURI().getScheme();
            String dataIndexWsURL = ("https".equals(scheme) ? "wss:" : "ws:") + dataIndexURL.substring(scheme.length() + 1);

            resource = vertx.fileSystem()
            .readFileBlocking("META-INF/resources/index.html")
            .toString(UTF_8)
            .replace("__GRAPHQL_HTTP_ENDPOINT__", "\"" + dataIndexURL + "/graphql\"")
            .replace("__GRAPHQL_WS_ENDPOINT__", "\"" + dataIndexWsURL + "/graphql\"");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Route(path = "/", methods = GET)
    public void handle(RoutingContext context) {
        try {
            context.response()
                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf8")
                    .end(resource);
        } catch (Exception ex) {
            ex.printStackTrace();
            context.fail(500, ex);
        }
    }
}
