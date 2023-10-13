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
package org.kie.kogito.examples;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Response {

    enum Type {
        SUCCESS,
        ERROR
    }

    private Type type;
    private String resourceId;

    public Response() {
    }

    public Response(Type type, String resourceId) {
        this.type = type;
        this.resourceId = resourceId;
    }

    public static Response success(String payload) {
        return new Response(Type.SUCCESS, payload);
    }

    public static Response error(String payload) {
        return new Response(Type.ERROR, payload);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Type.SUCCESS.equals(type);
    }
}