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

import java.time.ZonedDateTime;

public class QueryRecord {

    public static final String PENDING = "PENDING";
    public static final String RESOLVED = "RESOLVED";
    public static final String ERROR = "ERROR";

    private String processInstanceId;
    private ZonedDateTime created;
    private String status;
    private String query;
    private String answer;
    private ZonedDateTime lastModified;

    public QueryRecord() {
    }

    public QueryRecord(String processInstanceId, ZonedDateTime created, String status, String query, String answer, ZonedDateTime lastModified) {
        this.processInstanceId = processInstanceId;
        this.created = created;
        this.status = status;
        this.query = query;
        this.answer = answer;
        this.lastModified = lastModified;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String id) {
        this.processInstanceId = id;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "QueryRecord{" +
                "processInstanceId='" + processInstanceId + '\'' +
                ", created=" + created +
                ", status='" + status + '\'' +
                ", query='" + query + '\'' +
                ", answer='" + answer + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }
}
