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

import io.quarkus.arc.DefaultBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This default implementation is used when the persistence is not enabled.
 */
@DefaultBean
@ApplicationScoped
public class InMemoryQueryRequestRepository implements QueryRequestRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryQueryRequestRepository.class);

    private final Map<String, QueryRequest> queryRequestMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("The {} repository will be used. " +
                "You can build the application with the persistence profile to use a PostgreSQL database. " +
                "Read the project documentation for more information.", InMemoryQueryRequestRepository.class.getName());
    }

    @Override
    public void saveOrUpdate(QueryRequest queryRequest) {
        queryRequestMap.put(queryRequest.getProcessInstanceId(), queryRequest);
    }

    @Override
    public void delete(String id) {
        queryRequestMap.remove(id);
    }

    @Override
    public List<QueryRequest> getAll() {
        return new ArrayList<>(queryRequestMap.values());
    }
}
