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
package org.acme.deals;

import io.quarkus.runtime.Startup;
import io.smallrye.common.annotation.Identifier;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Startup
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Inject
    @Identifier("default-kafka-broker")
    Map<String, Object> kafkaConfig;

    @PostConstruct
    public void init() throws Exception {
        try (AdminClient client = AdminClient.create(kafkaConfig)) {
            Set<String> topics = client.listTopics().names().get(1, TimeUnit.MINUTES);

            List<NewTopic> newTopics = asList("kogito.process.dealreviews", "kogito.process.deals").stream().filter(t -> !topics.contains(t)).map(t -> new NewTopic(t, 1, (short) 1)).collect(toList());
            if (newTopics.isEmpty() == false) {
                client.createTopics(newTopics).all().get(1, TimeUnit.MINUTES);
                LOGGER.info("Created kogito.process.dealreviews and kogito.process.deals topics in Kafka");
            }
        }
    }
}
