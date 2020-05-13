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
package org.acme.travel;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import static java.util.Collections.singletonMap;

public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestResource.class);
    public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private KafkaContainer kafka;

    @Override
    public Map<String, String> start() {
        kafka = new KafkaContainer().withNetwork(Network.newNetwork()).withLogConsumer(new Slf4jLogConsumer(LOGGER));

        if (kafka.isRunning() == false) {
            kafka.start();
            LOGGER.info("Kafka servers: {}", kafka.getBootstrapServers());
        }
        return singletonMap(KAFKA_BOOTSTRAP_SERVERS, kafka.getBootstrapServers());
    }

    public KafkaContainer getKafka() {
        return kafka;
    }

    @Override
    public void stop() {
        kafka.stop();
    }
}
