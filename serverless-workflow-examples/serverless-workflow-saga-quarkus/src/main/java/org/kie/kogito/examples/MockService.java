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

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MockService {

    private static Logger LOGGER = LoggerFactory.getLogger(MockService.class);

    public Response execute(String failClass, Class clazz, boolean throwException, String resourceId) {
        boolean fail = Optional.ofNullable(clazz)
                .map(Class::getSimpleName)
                .map(n -> Objects.equals(failClass, n))
                .orElse(false);
        if (fail) {
            LOGGER.error("Error in {} for {}", failClass, resourceId);
        }
        if (fail && throwException) {
            throw new ServiceException("Error executing " + failClass + " for " + resourceId);
        }
        return new Response(fail ? Response.Type.ERROR : Response.Type.SUCCESS,
                UUID.randomUUID().toString());
    }
}
