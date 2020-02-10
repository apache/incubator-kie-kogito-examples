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

@ApplicationScoped
public class VertxRouter {

    @Inject
    @ConfigProperty(name = "kogito.dataindex.url", defaultValue = "http://localhost:8180")
    private String dataIndexURL;

    @Inject
    private Vertx vertx;

    private String resource;

    @PostConstruct
    public void init() {
        resource = vertx.fileSystem()
                .readFileBlocking("META-INF/resources/index.html")
                .toString(UTF_8)
                .replace("__GRAPHIQL_ENDPOINT__", "\"" + dataIndexURL + "/graphql\"");
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
