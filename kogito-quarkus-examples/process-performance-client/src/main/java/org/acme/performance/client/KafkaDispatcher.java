/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.acme.performance.client;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.kie.kogito.event.process.ProcessDataEvent;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaDispatcher implements RequestDispatcher {

    private class ObjectCloudEvent extends ProcessDataEvent<Object> {
        public ObjectCloudEvent(String trigger, Object data) {
            super(trigger, "java_client", data, null, null, null, null, null, null, null, null, null, null, null);
        }
    }

    private String trigger;
    private ObjectMapper objectMapper;
    private KafkaProducer<byte[], byte[]> kafkaProducer;

    public KafkaDispatcher(String trigger) {
        this.trigger = trigger;
        this.objectMapper = ObjectMapperFactory.get();
        Map<String, Object> properties = Collections.singletonMap(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        kafkaProducer = new KafkaProducer<>(properties, new ByteArraySerializer(), new ByteArraySerializer());
    }

    @Override
    public void dispatch(long delay, Consumer<Throwable> consumer) {
        try {
            kafkaProducer.send(new ProducerRecord<>("test", objectMapper.writeValueAsBytes(new ObjectCloudEvent(trigger, delay))), (r, e) -> {
                if (e != null) {
                    consumer.accept(e);
                }
            });
        } catch (JsonProcessingException e) {
            consumer.accept(e);
        }
    }

    @Override
    public void close() {
        kafkaProducer.close();
    }

}
