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

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestDispatcherRunner implements Callable<Void> {
    private final static Logger logger = LoggerFactory.getLogger(RequestDispatcherRunner.class);

    private static class ThrowableConsumer implements Consumer<Throwable> {
        private Collection<Throwable> throwables = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void accept(Throwable t) {
            throwables.add(t);
        }

        public int errorCount() {
            return throwables.size();
        }

        @Override
        public String toString() {
            return throwables.toString();
        }
    }

    private RequestDispatcher dispatcher;
    private long delay;
    private int numRequest;
    private ObjectMapper mapper = new ObjectMapper();
    private SortedSet<Instant> endTimes = new TreeSet<>();

    public RequestDispatcherRunner(RequestDispatcher dispatcher, int numRequest, long delay) {
        this.dispatcher = dispatcher;
        this.delay = delay;
        this.numRequest = numRequest;
    }

    @Override
    public Void call() throws Exception {
        int consumedRequest = 0;
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");

        try (KafkaConsumer<byte[], byte[]> kafkaConsumer = new KafkaConsumer<>(properties, new ByteArrayDeserializer(),
                new ByteArrayDeserializer());) {
            long startDispatch = System.currentTimeMillis();

            ThrowableConsumer errorCounter = new ThrowableConsumer();
            for (int i = 0; i < numRequest; i++) {
                dispatcher.dispatch(delay, errorCounter);
            }
            long endDispatch = System.currentTimeMillis();
            kafkaConsumer.subscribe(Collections.singleton("done"));
            while (numRequest > consumedRequest + errorCounter.errorCount()) {
                logger.info("Consumed request: {}", consumedRequest);
                ConsumerRecords<byte[], byte[]> events = kafkaConsumer.poll(Duration.ofSeconds(1));
                events.forEach(this::collectTime);
                consumedRequest += events.count();
            }
            if (errorCounter.errorCount() > 0) {
                logger.error(errorCounter.toString());
            }
            logger.info("Time dispatching {}", endDispatch - startDispatch);
            logger.info("Time from first finish to last finish {}", Duration.between(endTimes.first(), endTimes.last()));
        }
        return null;
    }

    private void collectTime(ConsumerRecord<byte[], byte[]> record) {

        try {
            String toParse = mapper.readValue(record.value(), Map.class).get("time").toString();
            endTimes.add(Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(toParse)));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
