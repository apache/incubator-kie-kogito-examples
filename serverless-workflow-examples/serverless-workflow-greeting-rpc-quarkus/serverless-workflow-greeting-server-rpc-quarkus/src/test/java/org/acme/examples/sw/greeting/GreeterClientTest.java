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

package org.acme.examples.sw.greeting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.acme.examples.sw.greeting.Greeting.HelloReply;
import org.acme.examples.sw.greeting.Greeting.HelloRequest;
import org.acme.examples.sw.greeting.Greeting.HelloRequest.Builder;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GreeterClientTest {

    private static final int port = Integer.getInteger("grpc.port", 50053);

    private static Server server;
    private static ManagedChannel channel;

    @BeforeAll
    static void setup() throws IOException {
        server = GreeterService.buildServer(port);
        server.start();
        channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        channel.shutdownNow();
        server.shutdownNow();
        if (!channel.awaitTermination(30, TimeUnit.SECONDS)) {
            throw new RuntimeException("Channel not terminated!");
        }
        server.awaitTermination();
        server = null;
        channel = null;
    }

    @Test
    public void testGreeting() {
        Builder request = HelloRequest.newBuilder().setName("Javierito");
        request.setLanguage("Spanish");
        HelloReply reply = GreeterGrpc.newBlockingStub(channel).sayHello(request.build());
        assertEquals("Saludos desde gRPC service Javierito", reply.getMessage());
    }

    @Test
    public void testGreetingAllLanguages() {
        Builder request = HelloRequest.newBuilder().setName("Javierito");
        Iterator<HelloReply> reply = GreeterGrpc.newBlockingStub(channel).sayHelloAllLanguages(request.build());
        assertEquals("Hello from gRPC service Javierito", reply.next().getMessage());
        assertEquals("Saludos desde gRPC service Javierito", reply.next().getMessage());
        assertFalse(reply.hasNext());
    }

    @Test
    public void testGreetingMultipleLanguagesAtOnce() throws InterruptedException {
        List<String> replies = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<HelloReply> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(HelloReply helloReply) {
                replies.add(helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };
        Builder request = HelloRequest.newBuilder().setName("Javierito");
        StreamObserver<HelloRequest> requestObserver = GreeterGrpc.newStub(channel).sayHelloMultipleLanguagesAtOnce(responseObserver);
        requestObserver.onNext(request.setLanguage("Spanish").build());
        requestObserver.onNext(request.setName("John").setLanguage("English").build());
        requestObserver.onNext(request.setName("Jan").setLanguage("Czech").build());
        requestObserver.onCompleted();
        latch.await();

        assertEquals(1, replies.size());
        assertEquals("Saludos desde gRPC service Javierito\n" +
                "Hello from gRPC service John\n" +
                "Hello from gRPC service Jan", replies.get(0));
    }

    @Test
    public void testGreetingMultipleLanguages() throws InterruptedException {
        List<String> replies = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<HelloReply> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(HelloReply helloReply) {
                replies.add(helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };
        Builder request = HelloRequest.newBuilder().setName("Javierito");
        StreamObserver<HelloRequest> requestObserver = GreeterGrpc.newStub(channel).sayHelloMultipleLanguages(responseObserver);
        requestObserver.onNext(request.setLanguage("Spanish").build());
        requestObserver.onNext(request.setName("John").setLanguage("English").build());
        requestObserver.onNext(request.setName("Jan").setLanguage("Czech").build());
        requestObserver.onCompleted();
        latch.await();

        assertEquals(3, replies.size());
        assertEquals("Saludos desde gRPC service Javierito", replies.get(0));
        assertEquals("Hello from gRPC service John", replies.get(1));
        assertEquals("Hello from gRPC service Jan", replies.get(2));
    }

    @Test
    public void testGreetingMultipleLanguagesError() throws InterruptedException {
        List<String> replies = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Status> error = new AtomicReference<>();
        StreamObserver<HelloReply> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(HelloReply helloReply) {
                replies.add(helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                error.set(Status.fromThrowable(throwable));
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };
        Builder request = HelloRequest.newBuilder().setName("Javierito");
        StreamObserver<HelloRequest> requestObserver = GreeterGrpc.newStub(channel).sayHelloMultipleLanguagesError(responseObserver);
        requestObserver.onNext(request.setLanguage("Spanish").build());
        requestObserver.onNext(request.setName("John").setLanguage("English").build());
        requestObserver.onNext(request.setName("Jan").setLanguage("Czech").build());
        requestObserver.onCompleted();
        latch.await();

        assertEquals(2, replies.size());
        assertEquals("Saludos desde gRPC service Javierito", replies.get(0));
        assertEquals("Hello from gRPC service John", replies.get(1));
        assertEquals(Status.OUT_OF_RANGE, error.get());
    }
}
