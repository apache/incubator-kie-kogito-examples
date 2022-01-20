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

package org.acme;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
 * This PostgreSqlQueryRecordRepository implementation is used when the application is build with the persistence profile.
 */
@ApplicationScoped
@Startup
@IfBuildProperty(name = "kogito.persistence.type", stringValue = "postgresql")
public class PostgreSqlQueryRecordRepository implements QueryRecordRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlQueryRecordRepository.class);

    private static final String QUERY_RECORD_TABLE = "queryrecord";

    private static final String QUERY_RECORD_COLUMNS = "processinstanceid, query, created, status, answer, lastmodified";

    private static final String CREATE_QUERY_RECORD_TABLE = "CREATE TABLE IF NOT EXISTS public.queryrecord\n" +
            "(\n" +
            "    processinstanceid character varying NOT NULL,\n" +
            "    query character varying,\n" +
            "    created timestamp without time zone,\n" +
            "    status character varying,\n" +
            "    answer character varying,\n" +
            "    lastmodified timestamp without time zone,\n" +
            "    CONSTRAINT queryrecord_pkey PRIMARY KEY (processinstanceid)\n" +
            ")";

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @PostConstruct
    public void initDB() {
        try {
            LOGGER.debug("Initializing {} table.", QUERY_RECORD_TABLE);
            client.query(CREATE_QUERY_RECORD_TABLE).execute().await().indefinitely();
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during " + QUERY_RECORD_TABLE + " initialization: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveOrUpdate(QueryRecord queryRecord) {
        client.preparedQuery("INSERT INTO " + QUERY_RECORD_TABLE + " (" + QUERY_RECORD_COLUMNS + ") " +
                " VALUES ($1, $2, $3, $4, $5, $6) " +
                "ON CONFLICT (processinstanceid) DO " +
                "UPDATE SET query = $2, created = $3, status = $4, answer = $5, lastmodified = $6 " +
                "RETURNING " + QUERY_RECORD_COLUMNS)
                .execute(Tuple.tuple(Stream.of(
                        queryRecord.getProcessInstanceId(),
                        queryRecord.getQuery(),
                        queryRecord.getCreated().toLocalDateTime(),
                        queryRecord.getStatus(),
                        queryRecord.getAnswer(),
                        queryRecord.getLastModified().toLocalDateTime()).collect(toList())))
                .await().indefinitely().value();
    }

    @Override
    public QueryRecord get(String id) {
        return client.preparedQuery("SELECT " + QUERY_RECORD_COLUMNS + " FROM " + QUERY_RECORD_TABLE + " WHERE processinstanceid = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .await().indefinitely();
    }

    private static QueryRecord from(Row row) {
        QueryRecord queryRecord = new QueryRecord();
        queryRecord.setProcessInstanceId(row.getString("processinstanceid"));
        queryRecord.setQuery(row.getString("query"));
        queryRecord.setCreated(ZonedDateTime.of(row.getLocalDateTime("created"), ZoneId.systemDefault()));
        queryRecord.setStatus(row.getString("status"));
        queryRecord.setAnswer(row.getString("answer"));
        queryRecord.setLastModified(ZonedDateTime.of(row.getLocalDateTime("lastmodified"), ZoneId.systemDefault()));
        return queryRecord;
    }

    @Override
    public List<QueryRecord> getAll() {
        RowIterator<Row> rows = client.query("SELECT " + QUERY_RECORD_COLUMNS + " FROM " + QUERY_RECORD_TABLE).execute()
                .onItem().transform(RowSet::iterator)
                .await().indefinitely();
        return StreamSupport.stream(((Iterable<Row>) () -> rows).spliterator(), false)
                .map(PostgreSqlQueryRecordRepository::from)
                .collect(toList());
    }
}
