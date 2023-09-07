/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.examples;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Inject
    MockService mockService;

    public Response processPayment(String orderId, String failService) {
        LOGGER.info("Process Payment for order {}", orderId);
        return mockService.execute(failService, PaymentService.class, true, orderId);
    }

    public Response processPayment(String orderId, String failService, String throwException) {
        LOGGER.info("Process Payment for order {}", orderId);
        return mockService.execute(failService, PaymentService.class, Boolean.parseBoolean(throwException), orderId);
    }

    public Response cancelPayment(String id) {
        LOGGER.info("Cancel Payment {}", id);
        return new Response(Response.Type.SUCCESS, id);
    }
}
