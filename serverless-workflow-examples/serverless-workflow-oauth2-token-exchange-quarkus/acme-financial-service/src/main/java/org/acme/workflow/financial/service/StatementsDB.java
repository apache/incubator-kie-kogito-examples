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
package org.acme.workflow.financial.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;

@ApplicationScoped
public class StatementsDB {

    private static final Map<String, List<StatementEntry>> STATEMENT_ENTRY = new HashMap<>();

    @Inject
    CustomersDB customersDB;

    @PostConstruct
    void initialize() {

        customersDB.getUserNames().forEach(c -> {
            List<StatementEntry> statementEntries = new ArrayList<>();

            if (Objects.equals(c, CustomersDB.ALICE)) {
                statementEntries.add(new StatementEntry(50.00, "2024-03-17"));
                statementEntries.add(new StatementEntry(11.00, "2024-03-18"));
                statementEntries.add(new StatementEntry(12.00, "2024-03-19"));
                statementEntries.add(new StatementEntry(13.00, "2024-03-20"));
                statementEntries.add(new StatementEntry(14.00, "2024-03-21"));
            } else {
                statementEntries.add(new StatementEntry(1.00, "2024-03-17"));
            }


            STATEMENT_ENTRY.put(c, statementEntries);
        });
    }

    public List<StatementEntry> getStatementEntries(String customerUserName) {
        return Collections.unmodifiableList(STATEMENT_ENTRY.get(customerUserName));
    }
}
