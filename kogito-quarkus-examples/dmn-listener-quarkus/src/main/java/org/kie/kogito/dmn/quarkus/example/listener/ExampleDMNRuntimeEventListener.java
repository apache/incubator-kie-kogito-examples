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
package org.kie.kogito.dmn.quarkus.example.listener;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class demonstrates one of the two methods offered by Kogito to inject custom
 * {@link org.kie.dmn.api.core.event.DMNRuntimeEventListener}s in its internal {@link org.kie.dmn.api.core.DMNRuntime}.
 * <p>
 * This is the quickest one to inject a single listener and it works by creating a standard listener class
 * (a class that implements {@link org.kie.dmn.api.core.event.DMNRuntimeEventListener} interface) and annotating it
 * with {@link ApplicationScoped}.
 * <p>
 * The second injection method is explained in {@link ExampleDecisionEventListenerConfig}.
 * All the listeners instantiated with both methods will be injected during the application startup phase.
 */
@ApplicationScoped
public class ExampleDMNRuntimeEventListener extends LoggingDMNRuntimeEventListener {

    public ExampleDMNRuntimeEventListener() {
        super("ExampleDMNRuntimeEventListener");
    }

}
