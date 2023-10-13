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
package org.acme.deals;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;
import io.smallrye.common.annotation.Identifier;

import static java.util.Collections.singleton;

@ApplicationScoped
@Startup
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final String KOGITO_PROCESS = "kogito.process";

    @Inject
    @Identifier("default-kafka-broker")
    Map<String, Object> kafkaConfig;

    @PostConstruct
    public void init() throws Exception {
        try (AdminClient client = AdminClient.create(kafkaConfig)) {
            Set<String> topics = client.listTopics().names().get(1, TimeUnit.MINUTES);

            if (!topics.contains(KOGITO_PROCESS)) {
                client.createTopics(singleton(new NewTopic(KOGITO_PROCESS, 1, (short) 1))).all().get(1, TimeUnit.MINUTES);
                LOGGER.info("Created kogito.process topic in Kafka");
            }
        }
    }
}
