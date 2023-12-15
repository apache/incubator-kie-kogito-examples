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
package org.acme;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.Startup;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import static java.util.stream.Collectors.toList;

/**
 * This PostgreSqlQueryRequestRepository implementation is used when the application is built with the persistence profile.
 */
@ApplicationScoped
@Startup
@IfBuildProperty(name = "quarkus.datasource.db-kind", stringValue = "postgresql")
public class PostgreSqlQueryRequestRepository implements QueryRequestRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlQueryRequestRepository.class);

    private static final String QUERY_REQUEST_TABLE = "queryrequest";

    private static final String CREATE_QUERY_REQUEST_TABLE = "CREATE TABLE IF NOT EXISTS public.queryrequest\n" +
            "(\n" +
            "    processinstanceid character varying NOT NULL,\n" +
            "    query character varying,\n" +
            "    CONSTRAINT queryrequest_pkey PRIMARY KEY (processinstanceid)\n" +
            ")";

    private static final String QUERY_REQUEST_COLUMNS = "processinstanceid, query";

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @PostConstruct
    public void initDB() {
        try {
            LOGGER.debug("Initializing {} table.", QUERY_REQUEST_TABLE);
            client.query(CREATE_QUERY_REQUEST_TABLE).execute().await().indefinitely();
        } catch (Exception e) {
            throw new QueryServiceException("An error was produced during " + QUERY_REQUEST_TABLE + " initialization: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveOrUpdate(QueryRequest queryRequest) {
        client.preparedQuery("INSERT INTO " + QUERY_REQUEST_TABLE + " (" + QUERY_REQUEST_COLUMNS + ") " +
                " VALUES ($1, $2) " +
                "ON CONFLICT (processinstanceid) DO " +
                "UPDATE SET query = $2 " +
                "RETURNING " + QUERY_REQUEST_COLUMNS)
                .execute(Tuple.tuple(Stream.of(
                        queryRequest.getProcessInstanceId(),
                        queryRequest.getQuery()).collect(toList())))
                .await().indefinitely().value();
    }

    @Override
    public void delete(String id) {
        client.preparedQuery("DELETE FROM " + QUERY_REQUEST_TABLE + " WHERE processinstanceid = $1")
                .execute(Tuple.of(id))
                .await().indefinitely();
    }

    @Override
    public List<QueryRequest> getAll() {
        RowIterator<Row> rows = client.query("SELECT " + QUERY_REQUEST_COLUMNS + " FROM " + QUERY_REQUEST_TABLE).execute()
                .onItem().transform(RowSet::iterator)
                .await().indefinitely();
        return StreamSupport.stream(((Iterable<Row>) () -> rows).spliterator(), false)
                .map(PostgreSqlQueryRequestRepository::from)
                .collect(toList());
    }

    private static QueryRequest from(Row row) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setProcessInstanceId(row.getString("processinstanceid"));
        queryRequest.setQuery(row.getString("query"));
        return queryRequest;
    }
}
