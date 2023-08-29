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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.Startup;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.annotation.PostConstruct;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@Startup
@IfBuildProperty(name = "enable.resource.postgresql", stringValue = "true")
public class PostgreSqlSubscriptionRepository implements SubscriptionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlSubscriptionRepository.class);

    private static final String SUBSCRIPTION_TABLE = "newsletter_sub";

    public static final String SUBSCRIPTION_COLUMNS = "processinstanceid, email, nm, verified";

    private static final String CREATE_QUERY_SUBSCRIPTION_TABLE = "CREATE TABLE IF NOT EXISTS public." + SUBSCRIPTION_TABLE + "\n" +
            "(\n" +
            "    processinstanceid character varying NOT NULL,\n" +
            "    email character varying NOT NULL,\n" +
            "    nm character varying,\n" +
            "    verified boolean NOT NULL DEFAULT false,\n" +
            "    CONSTRAINT " + SUBSCRIPTION_TABLE + "_pkey PRIMARY KEY (processinstanceid)\n" +
            ")";

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @PostConstruct
    public void initDB() {
        try {
            LOGGER.info("Initializing {} table.", SUBSCRIPTION_TABLE);
            client.query(CREATE_QUERY_SUBSCRIPTION_TABLE).execute().await().indefinitely();
        } catch (Exception e) {
            throw new SubscriptionException("An error was produced during " + SUBSCRIPTION_TABLE + " initialization: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Subscription> fetchByEmail(String email) {
        return (Optional<Subscription>) client.preparedQuery("SELECT " + SUBSCRIPTION_COLUMNS + " FROM " + SUBSCRIPTION_TABLE + " WHERE email = $1")
                .execute(Tuple.of(email))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(it -> it.hasNext() ? Optional.of(from(it.next())) : Optional.empty())
                .await().indefinitely();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Subscription> fetchByIdAndEmail(String id, String email) {
        return (Optional<Subscription>) client.preparedQuery("SELECT " + SUBSCRIPTION_COLUMNS + " FROM " + SUBSCRIPTION_TABLE + " WHERE processinstanceid = $1 AND email = $2")
                .execute(Tuple.of(id, email))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(it -> it.hasNext() ? Optional.of(from(it.next())) : Optional.empty())
                .await().indefinitely();
    }

    @Override
    public List<Subscription> fetchAllByVerified(boolean verified) {
        return client.preparedQuery("SELECT " + SUBSCRIPTION_COLUMNS + " FROM " + SUBSCRIPTION_TABLE + " WHERE verified = $1")
                .execute(Tuple.of(verified))
                .onItem().transform(RowSet::iterator)
                .map(PostgreSqlSubscriptionRepository::from)
                .await().indefinitely();
    }

    @Override
    public void saveOrUpdate(Subscription subscription) {
        client.preparedQuery("INSERT INTO " + SUBSCRIPTION_TABLE + " (" + SUBSCRIPTION_COLUMNS + ") " +
                                     " VALUES ($1, $2, $3, $4) " +
                                     "ON CONFLICT (processinstanceid) DO " +
                                     "UPDATE SET email = $2, nm = $3, verified = $4 " +
                                     "RETURNING " + SUBSCRIPTION_COLUMNS)
                .execute(Tuple.tuple(Stream.of(
                        subscription.getId(),
                        subscription.getEmail(),
                        subscription.getName(),
                        subscription.isVerified()).collect(toList())))
                .await().indefinitely().value();
    }

    @Override
    public void delete(String id) {
        client.preparedQuery("DELETE FROM " + SUBSCRIPTION_TABLE + " WHERE processinstanceid = $1 ")
                .execute(Tuple.tuple(Collections.singletonList(id)))
                .await().indefinitely().value();
    }

    private static Subscription from(Row row) {
        Subscription subscription = new Subscription(row.getString("email"));
        subscription.setId(row.getString("processinstanceid"));
        subscription.setVerified(row.getBoolean("verified"));
        subscription.setName(row.getString("nm"));
        return subscription;
    }

    private static List<Subscription> from(RowIterator<Row> rows) {
        return StreamSupport.stream(((Iterable<Row>) () -> rows).spliterator(), false)
                .map(PostgreSqlSubscriptionRepository::from)
                .collect(toList());
    }
}
