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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.Startup;

/**
 * This PostgreSqlQueryRecordRepository implementation is used when the application is build with the persistence profile.
 */
@ApplicationScoped
@Startup
@IfBuildProperty(name = "kogito.persistence.type", stringValue = "jdbc")
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
    DataSource dataSource;

    @PostConstruct
    public void initDB() {
        try (Connection connection = dataSource.getConnection(); PreparedStatement st = connection.prepareStatement(CREATE_QUERY_RECORD_TABLE)) {
            LOGGER.debug("Initializing {} table.", QUERY_RECORD_TABLE);
            st.execute();
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during " + QUERY_RECORD_TABLE + " initialization: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveOrUpdate(QueryRecord queryRecord) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("INSERT INTO " + QUERY_RECORD_TABLE + " (" + QUERY_RECORD_COLUMNS + ") " +
                        " VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON CONFLICT (processinstanceid) DO " +
                        "UPDATE SET query = ?, created = ?, status = ?, answer = ?, lastmodified = ?")) {
            st.setString(1, queryRecord.getProcessInstanceId());
            st.setString(2, queryRecord.getQuery());
            st.setObject(3, queryRecord.getCreated().toLocalDateTime());
            st.setString(4, queryRecord.getStatus());
            st.setString(5, queryRecord.getAnswer());
            st.setObject(6, queryRecord.getLastModified().toLocalDateTime());
            st.setString(7, queryRecord.getQuery());
            st.setObject(8, queryRecord.getCreated().toLocalDateTime());
            st.setString(9, queryRecord.getStatus());
            st.setString(10, queryRecord.getAnswer());
            st.setObject(11, queryRecord.getLastModified().toLocalDateTime());
            st.executeUpdate();
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during " + QUERY_RECORD_TABLE + " update: " + e.getMessage(), e);
        }

    }

    @Override
    public QueryRecord get(String id) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("SELECT " + QUERY_RECORD_COLUMNS + " FROM " + QUERY_RECORD_TABLE + " WHERE processinstanceid = ?")) {
            st.setString(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return from(rs);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during " + QUERY_RECORD_TABLE + " find: " + e.getMessage(), e);
        }
    }

    private static QueryRecord from(ResultSet row) throws SQLException {
        QueryRecord queryRecord = new QueryRecord();
        queryRecord.setProcessInstanceId(row.getString("processinstanceid"));
        queryRecord.setQuery(row.getString("query"));
        queryRecord.setCreated(ZonedDateTime.of(row.getObject("created", LocalDateTime.class), ZoneId.systemDefault()));
        queryRecord.setStatus(row.getString("status"));
        queryRecord.setAnswer(row.getString("answer"));
        queryRecord.setLastModified(ZonedDateTime.of(row.getObject("lastmodified", LocalDateTime.class), ZoneId.systemDefault()));
        return queryRecord;
    }

    @Override
    public List<QueryRecord> getAll() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("SELECT " + QUERY_RECORD_COLUMNS + " FROM " + QUERY_RECORD_TABLE)) {
            List<QueryRecord> result = new ArrayList<>();
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(from(rs));
                }
            }
            return result;
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during " + QUERY_RECORD_TABLE + " find all: " + e.getMessage(), e);
        }
    }
}
