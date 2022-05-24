/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.examples.sw.greeting;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.sw.greeting.Greeting.HelloReply;
import org.kie.kogito.examples.sw.greeting.Greeting.HelloRequest;
import org.kie.kogito.examples.sw.greeting.Greeting.HelloRequest.Builder;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreeterClient {

    private static final int port = Integer.getInteger("grpc.port", 50053);

    private static Server server;

    @BeforeAll
    static void setup() throws IOException {
        server = GreeterService.buildServer(port);
        server.start();
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        server.shutdownNow();
        server.awaitTermination();
        server = null;
    }

    @Test
    public void testGreeting() {
        Builder request = HelloRequest.newBuilder().setName("Javierito");
        ManagedChannelBuilder<?> channel = ManagedChannelBuilder.forAddress("localhost", port);
        channel.usePlaintext();
        request.setLanguage("Spanish");
        HelloReply reply = GreeterGrpc.newBlockingStub(channel.build()).sayHello(request.build());
        assertEquals("Saludos desde gRPC service Javierito", reply.getMessage());
    }
}
