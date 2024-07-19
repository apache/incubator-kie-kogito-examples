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
package org.kie.kogito.legacy;

public class Hello implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    @org.kie.api.definition.type.Label("Greeting")
    private java.lang.String greeting;

    @org.kie.api.definition.type.Label(value = "Approved")
    private java.lang.Boolean approved;

    public Hello() {
    }

    public java.lang.String getGreeting() {
        return this.greeting;
    }

    public void setGreeting(java.lang.String greeting) {
        this.greeting = greeting;
    }

    public java.lang.Boolean getApproved() {
        return this.approved;
    }

    public void setApproved(java.lang.Boolean approved) {
        this.approved = approved;
    }

    public Hello(java.lang.String greeting, java.lang.Boolean approved) {
        this.greeting = greeting;
        this.approved = approved;
    }

}