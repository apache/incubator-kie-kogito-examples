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
package org.acme.newsletter.subscription.service;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SubscriptionServiceImpl implements SubscriptionService {

    @Inject
    SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription confirm(Subscription subscription) {
        final Optional<Subscription> unconfirmedSub = subscriptionRepository.fetchByIdAndEmail(subscription.getId(), subscription.getEmail());
        if (unconfirmedSub.isEmpty()) {
            throw new SubscriptionException("Impossible to confirm subscription for email " + subscription.getEmail() + ". This email doesn't exist in the database.");
        }
        subscription.setVerified(true);
        subscriptionRepository.saveOrUpdate(subscription);
        return subscription;
    }

    @Override
    public boolean checkEmail(String email) {
        final Optional<Subscription> subscription = subscriptionRepository.fetchByEmail(email);
        return subscription.isPresent();
    }

    @Override
    public Subscription subscribe(Subscription subscription) {
        subscription.setVerified(false);
        subscriptionRepository.saveOrUpdate(subscription);
        return subscription;
    }

    @Override
    public void delete(String id) {
        subscriptionRepository.delete(id);
    }

    @Override
    public Optional<Subscription> fetch(String email) {
        return subscriptionRepository.fetchByEmail(email);
    }

    @Override
    public List<Subscription> list(boolean verified) {
        return subscriptionRepository.fetchAllByVerified(verified);
    }

}
