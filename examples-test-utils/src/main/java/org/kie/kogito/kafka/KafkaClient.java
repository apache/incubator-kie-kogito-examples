/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka client for Kogito Example tests.
 */
public class KafkaClient {

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaClient.class);

    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private AtomicBoolean shutdown = new AtomicBoolean(false);

    public KafkaClient(String hosts) {
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, this.getClass().getName() + "Producer");
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerConfig);

        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass().getName() + "Consumer");
        consumer = new KafkaConsumer<>(consumerConfig);
    }

    public void consume(String topic, Consumer<String> callback) {
        consumer.subscribe(Collections.singletonList(topic));

        CompletableFuture.runAsync(() -> {
            while (!shutdown.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

                StreamSupport.stream(records.spliterator(), true)
                             .map(ConsumerRecord::value)
                             .forEach(callback::accept);

                consumer.commitSync();
            }
            shutdownLatch.countDown();
        });
    }

    public void produce(String data, String topic) {
        producer.send(new ProducerRecord<>(topic, data), (m, ex) -> {
            Optional.ofNullable(ex).ifPresent(e -> LOGGER.error("Error publishing message {}", m, ex));
        });
    }

    public void shutdown() {
        CompletableFuture.runAsync(() -> producer.close());
        try {
            shutdown.set(true);
            shutdownLatch.await(1, TimeUnit.MINUTES);
            consumer.close();
        } catch (InterruptedException e) {
            LOGGER.error("Error shutting down kafka consumer/producer", e);
        }
    }
}
