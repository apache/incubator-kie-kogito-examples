package org.acme.travels;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.NativeImageTest;
import org.acme.test.KeycloakServerTestResource;

@NativeImageTest
@QuarkusTestResource(KeycloakServerTestResource.class)
public class NativeApprovalsRestIT extends ApprovalsRestIT {
    // run the same tests only against native image
}