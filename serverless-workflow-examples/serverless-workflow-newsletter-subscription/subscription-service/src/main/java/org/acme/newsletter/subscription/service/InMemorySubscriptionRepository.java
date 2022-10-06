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

package org.acme.newsletter.subscription.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.DefaultBean;

/**
 * This default implementation is used when the persistence is not enabled.
 */
@DefaultBean
@ApplicationScoped
public class InMemorySubscriptionRepository implements SubscriptionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySubscriptionRepository.class);

    private final Map<String, Subscription> subscriptionMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("The {} repository will be used. " +
                            "You can build the application with the persistence profile to use a PostgreSQL database. " +
                            "Read the project documentation for more information.", InMemorySubscriptionRepository.class.getName());
    }

    @Override
    public Optional<Subscription> fetchByEmail(String email) {
        return Optional.ofNullable(subscriptionMap.getOrDefault(email, null));
    }

    public Optional<Subscription> fetchByIdAndEmail(String id, String email) {
        final Optional<Subscription> subscription = Optional.ofNullable(subscriptionMap.getOrDefault(email, null));
        if (subscription.isPresent() && !id.equals(subscription.get().getId())) {
            return Optional.empty();
        }
        return subscription;
    }

    @Override
    public void saveOrUpdate(Subscription subscription) {
        this.subscriptionMap.put(subscription.getEmail(), subscription);
    }

    @Override
    public void delete(String id) {
        this.subscriptionMap.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(id))
                .findFirst()
                .ifPresent(entry -> subscriptionMap.remove(entry.getKey()));
    }

    @Override
    public List<Subscription> fetchAllByVerified(boolean verified) {
        return subscriptionMap.values().stream().filter(s -> s.isVerified() == verified).collect(Collectors.toUnmodifiableList());
    }
}
