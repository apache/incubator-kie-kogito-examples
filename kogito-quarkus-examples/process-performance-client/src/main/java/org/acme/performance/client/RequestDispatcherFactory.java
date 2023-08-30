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
package org.acme.performance.client;

public class RequestDispatcherFactory {

    private RequestDispatcherFactory() {
    }

    public enum RequestType {
        REST,
        REST_ASYNC,
        KAFKA
    }

    public static RequestDispatcher getDispatcher(RequestType type, String processId) {

        switch (type) {
            case REST:
                return new SyncRestDispatcher(processId);
            case REST_ASYNC:
                return new AsyncRestDispatcher(processId);
            case KAFKA:
                return new KafkaDispatcher(processId);
            default:
                throw new UnsupportedOperationException("Unknown type " + type);
        }

    }

}
