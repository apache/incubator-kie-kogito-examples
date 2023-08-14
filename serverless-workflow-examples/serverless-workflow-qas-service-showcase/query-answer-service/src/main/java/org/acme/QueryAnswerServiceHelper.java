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

import java.time.ZonedDateTime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import static org.acme.QueryRecord.ERROR;
import static org.acme.QueryRecord.PENDING;
import static org.acme.QueryRecord.RESOLVED;

/**
 * Helper class used from the SW workflow actions to update the queries and answers being constructed.
 */
@ApplicationScoped
public class QueryAnswerServiceHelper {

    private static final String QUERY_NODE = "query";
    private static final String ANSWER_NODE = "answer";
    private static final String ERROR_NODE = "error";

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryAnswerServiceHelper.class);

    @Inject
    QueryRecordRepository repository;

    public void registerQuery(JsonNode workflowData, KogitoProcessContext context) {
        String processInstanceId = context.getProcessInstance().getStringId();
        LOGGER.debug("Registering query for processInstance: {}, workflowData: {}", processInstanceId, workflowData);
        ZonedDateTime now = ZonedDateTime.now();
        String query = getNodeAsText(workflowData, QUERY_NODE);
        try {
            QueryRecord queryRecord = new QueryRecord(processInstanceId, now, PENDING, query, null, now);
            repository.saveOrUpdate(queryRecord);
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during query registering: " + e.getMessage(), e.getCause());
        }
    }

    public void registerAnswer(JsonNode workflowData, KogitoProcessContext context) {
        String processInstanceId = context.getProcessInstance().getStringId();
        LOGGER.debug("Registering answer for processInstance: {}, workflowData: {}", processInstanceId, workflowData);
        String answer = getNodeAsText(workflowData, ANSWER_NODE);
        try {
            updateQueryRecord(processInstanceId, RESOLVED, answer);
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during answer registering: " + e.getMessage(), e.getCause());
        }
    }

    public void registerError(JsonNode workflowData, KogitoProcessContext context) {
        String processInstanceId = context.getProcessInstance().getStringId();
        LOGGER.debug("Registering error for processInstance: {}, workflowData: {}", processInstanceId, workflowData);
        String error = getNodeAsText(workflowData, ERROR_NODE);
        try {
            updateQueryRecord(processInstanceId, ERROR, error);
        } catch (Exception e) {
            throw new QueryAnswerServiceException("An error was produced during answer registering: " + e.getMessage(), e.getCause());
        }
    }

    private void updateQueryRecord(String processInstanceId, String status, String answer) {
        QueryRecord queryRecord = repository.get(processInstanceId);
        if (queryRecord != null) {
            queryRecord.setStatus(status);
            queryRecord.setAnswer(answer);
            queryRecord.setLastModified(ZonedDateTime.now());
            repository.saveOrUpdate(queryRecord);
        } else {
            LOGGER.warn("No query record was found for processInstanceId: {}", processInstanceId);
        }
    }

    private static String getNodeAsText(JsonNode workflowData, String nodeName) {
        JsonNode node = workflowData.get(nodeName);
        if (node == null) {
            LOGGER.warn("Expected nodeName: {} was not found in workflowdata: {}", nodeName, workflowData);
        }
        return node != null ? node.textValue() : null;
    }
}
