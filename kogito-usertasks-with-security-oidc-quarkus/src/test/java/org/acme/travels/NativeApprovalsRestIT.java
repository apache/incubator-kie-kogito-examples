package org.acme.travels;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeApprovalsRestIT extends ApprovalsRestIT {
    // run the same tests only against native image
}