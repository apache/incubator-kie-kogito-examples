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
package org.kie.kogito.testcontainers;

import org.kie.kogito.resources.ConditionHolder;
import org.kie.kogito.resources.ConditionalTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/**
 * Kafka Container for Kogito examples.
 */
public class KogitoKafkaContainer extends KafkaContainer implements ConditionalTestResource<KogitoKafkaContainer> {

    public static final String NAME = "kafka";
    public static final String QUARKUS_KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    public static final String SPRINGBOOT_KAFKA_BOOTSTRAP_SERVERS = "spring.kafka.bootstrap-servers";

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoKafkaContainer.class);

    private final ConditionHolder condition = new ConditionHolder(NAME);

    public KogitoKafkaContainer() {
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
    }

    @Override
    public void start() {
        if (condition.isEnabled()) {
            super.start();

            System.setProperty(QUARKUS_KAFKA_BOOTSTRAP_SERVERS, getBootstrapServers());
            System.setProperty(SPRINGBOOT_KAFKA_BOOTSTRAP_SERVERS, getBootstrapServers());
            LOGGER.info("Kafka servers: {}", getBootstrapServers());
        }
    }

    @Override
    public void stop() {
        if (condition.isEnabled()) {
            super.stop();

            System.clearProperty(QUARKUS_KAFKA_BOOTSTRAP_SERVERS);
            System.clearProperty(SPRINGBOOT_KAFKA_BOOTSTRAP_SERVERS);
        }
    }

    @Override
    public KogitoKafkaContainer enableConditional() {
        condition.enableConditional();
        return this;
    }
}
