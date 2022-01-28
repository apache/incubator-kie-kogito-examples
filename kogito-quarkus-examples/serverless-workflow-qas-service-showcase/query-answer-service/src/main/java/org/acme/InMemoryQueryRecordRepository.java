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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class InMemoryQueryRecordRepository implements QueryRecordRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryQueryRecordRepository.class);

    private final Map<String, QueryRecord> queryRecordMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("The {} repository will be used. " +
                "You can build the application with the persistence profile to use a PostgreSQL database. " +
                "Read the project documentation for more information.", InMemoryQueryRecordRepository.class.getName());
    }

    @Override
    public void saveOrUpdate(QueryRecord queryRecord) {
        queryRecordMap.put(queryRecord.getProcessInstanceId(), queryRecord);
    }

    @Override
    public QueryRecord get(String processInstanceId) {
        return queryRecordMap.get(processInstanceId);
    }

    @Override
    public List<QueryRecord> getAll() {
        return new ArrayList<>(queryRecordMap.values());
    }
}
