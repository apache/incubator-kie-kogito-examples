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
package org.acme.travel;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.avro.AvroEventMarshaller;
import org.kie.kogito.event.avro.AvroEventUnmarshaller;
import org.kie.kogito.event.avro.AvroUtils;

@ApplicationScoped
public class AvroMarshallerProducer {

    private AvroUtils avroUtils;

    @PostConstruct
    void init() throws IOException {
        avroUtils = new AvroUtils();
    }

    @Produces
    EventMarshaller<byte[]> getAvroMarshaller() {
        return new AvroEventMarshaller(avroUtils);
    }

    @Produces
    EventUnmarshaller<byte[]> getAvroUnmarshaller() {
        return new AvroEventUnmarshaller(avroUtils);
    }

    // publish as bean for testing
    @Produces
    AvroUtils getAvroUtils() {
        return avroUtils;
    }

}
