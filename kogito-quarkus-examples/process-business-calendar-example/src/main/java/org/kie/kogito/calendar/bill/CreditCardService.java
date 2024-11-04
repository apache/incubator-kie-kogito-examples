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

package org.kie.kogito.calendar.bill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreditCardService {

    private Logger logger = LoggerFactory.getLogger(CreditCardService.class);

    public CreditCardDetails processCreditBill(String creditCardNumber) {
        logger.info("Paying credit card");
        return new CreditCardDetails(creditCardNumber);
    }

    public CreditCardDetails settleBill(CreditCardDetails creditCardDetails) {
        creditCardDetails.setStatus("Bill paid");
        logger.info("settling bill");
        return creditCardDetails;
    }

    public CreditCardDetails cancelPayment(CreditCardDetails creditCardDetails) {
        creditCardDetails.setStatus("Payment cancelled, money will be refunded if it it is debited");
        logger.info("cancelling bill");
        return creditCardDetails;
    }
}
