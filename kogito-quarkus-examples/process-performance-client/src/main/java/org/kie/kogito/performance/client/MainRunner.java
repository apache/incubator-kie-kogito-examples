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

package org.kie.kogito.performance.client;

import org.kie.kogito.performance.client.RequestDispatcherFactory.RequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainRunner.class);

    public static void main(String[] args) {
        try (RequestDispatcher dispatcher = RequestDispatcherFactory.getDispatcher(RequestType.KAFKA, "test")) {
            new RequestDispatcherRunner(dispatcher, 100, 10).call();
        } catch (Exception ex) {
            logger.error("Execution error ", ex);
            System.exit(-1);
        }
        System.exit(0);
    }
}
