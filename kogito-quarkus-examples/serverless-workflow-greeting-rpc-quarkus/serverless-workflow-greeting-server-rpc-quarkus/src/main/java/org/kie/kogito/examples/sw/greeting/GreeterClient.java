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

import org.kie.kogito.examples.sw.greeting.Greeting.HelloReply;
import org.kie.kogito.examples.sw.greeting.Greeting.HelloRequest;
import org.kie.kogito.examples.sw.greeting.Greeting.HelloRequest.Builder;

import io.grpc.ManagedChannelBuilder;

public class GreeterClient {
    public static void main(String[] args) {
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 50051;
        Builder request = HelloRequest.newBuilder().setName("Javierito");
        ManagedChannelBuilder<?> channel = ManagedChannelBuilder.forAddress("localhost", port);
        channel.usePlaintext();
        request.setLanguage("Spanish");
        HelloReply reply = GreeterGrpc.newBlockingStub(channel.build()).sayHello(request.build());
        System.out.println("Server messsage is " + reply.getMessage());
    }
}
