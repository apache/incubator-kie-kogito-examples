package org.acme.travels;

import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;


public class KeycloakContainerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakContainerTest.class);

    @ClassRule
    public static final GenericContainer keycloak =
            new FixedHostPortGenericContainer("quay.io/keycloak/keycloak:8.0.1")
                    .withFixedExposedPort(8281, 8080)
                    .withEnv("KEYCLOAK_USER", "admin")
                    .withEnv("KEYCLOAK_PASSWORD", "admin")
                    .withEnv("KEYCLOAK_IMPORT", "/tmp/realm.json")
                    .withClasspathResourceMapping("kogito-realm.json", "/tmp/realm.json", BindMode.READ_ONLY)
                    .withLogConsumer(new Slf4jLogConsumer(LOGGER))
                    .waitingFor(Wait.forHttp("/auth"));

}
