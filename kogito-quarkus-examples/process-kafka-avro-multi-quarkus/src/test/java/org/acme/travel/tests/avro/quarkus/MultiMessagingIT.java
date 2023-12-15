/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme.travel.tests.avro.quarkus;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.acme.travel.Traveller;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.avro.AvroIO;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.kafka.Record;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class MultiMessagingIT {

    private static final int count = 3;
    private static final CountDownLatch countDownLatch = new CountDownLatch(count);
    private static final Logger logger = LoggerFactory.getLogger(MultiMessagingIT.class);

    @Inject
    @Channel("travellers-out")
    Emitter<byte[]> emitter;

    @Inject
    AvroIO utils;

    @Incoming("processedtravellers-in")
    public CompletionStage<?> onProcessedEvent(Record<String, byte[]> message) throws IOException {
        logger.info("Event received for processed travellers");
        assertEquals("Real Betis Balompie", message.key());
        Traveller event = utils.readObject(message.value(), Traveller.class);
        logger.info("Event deserialized sucessfully {}", event);
        assertTraveller(event);
        countDownLatch.countDown();
        logger.info("Count down is {}", countDownLatch.getCount());
        return CompletableFuture.completedStage(null);
    }

    @Incoming("cancelled-in")
    public CompletionStage<?> onCancelledEvent(Message<byte[]> message) throws IOException {
        logger.info("Event received for cancelled travellers");
        Traveller event = utils.readObject(message.getPayload(), Traveller.class);
        logger.info("Event deserialized sucessfully {}", event);
        assertTraveller(event);
        countDownLatch.countDown();
        logger.info("Count down is {}", countDownLatch.getCount());
        return CompletableFuture.completedStage(null);
    }

    @Test
    public void testProcess() throws InterruptedException {
        IntStream.range(0, count)
                .mapToObj(i -> new Traveller("Name" + i, "LastName" + i, "email" + i, getNationality(i)))
                .forEach(traveller -> {
                    try {
                        emitter.send(utils.writeObject(traveller)).whenComplete(this::logEventOrFailure);
                    } catch (IOException e) {
                        logger.error("Error marshalling event", e);
                    }
                });
        countDownLatch.await(10, TimeUnit.SECONDS);
        assertEquals(0, countDownLatch.getCount());
    }

    private void logEventOrFailure(Void v, Throwable e) {
        if (e != null) {
            logger.error("Error when publishing event", e);
        } else {
            logger.info("Event published succesfully");
        }
    }

    private void assertTraveller(Traveller traveller) {
        assertEquals(!traveller.getNationality().equals("American"), traveller.isProcessed());
        assertTrue(traveller.getFirstName().matches("Name[0-9]+"));
        assertTrue(traveller.getLastName().matches("LastName[0-9]+"));
        assertTrue(traveller.getEmail().matches("email[0-9]+"));
    }

    private String getNationality(int i) {
        return i % 2 == 0 ? "American" : "Spanish";
    }

}
