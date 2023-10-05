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
package org.acme.serverless.examples;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.kie.kogito.addon.quarkus.messaging.common.ChannelFormat;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.avro.AvroCloudEventUnmarshallerFactory;
import org.kie.kogito.event.avro.AvroIO;

@ApplicationScoped
public class AvroMarshallerProducer {

    private AvroIO avroIO;

    @PostConstruct
    void init() throws IOException {
        avroIO = new AvroIO();
    }

    @Produces
    @Named("avro")
    @ChannelFormat
    public CloudEventUnmarshallerFactory<byte[]> getAvroCloudEventUnmarshallerFactory() {
        return new AvroCloudEventUnmarshallerFactory(avroIO);
    }

    @Produces
    AvroIO getAvroIO() {
        return avroIO;
    }

}